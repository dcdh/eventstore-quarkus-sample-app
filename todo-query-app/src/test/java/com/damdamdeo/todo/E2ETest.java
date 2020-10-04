package com.damdamdeo.todo;

import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import com.damdamdeo.eventsourced.encryption.infra.jackson.JacksonAggregateRootId;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.todo.domain.api.TodoStatus;
import com.damdamdeo.todo.infrastructure.TodoEntity;
import com.jayway.restassured.module.jsv.JsonSchemaValidator;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.junit.jupiter.api.*;
import org.keycloak.representations.AccessTokenResponse;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class E2ETest {

    @Inject
    KafkaDebeziumProducer kafkaDebeziumProducer;

    @Inject
    EntityManager entityManager;

    @Inject
    SecretStore secretStore;

    @Inject
    @DataSource("secret-store")
    AgroalDataSource secretStoreDataSource;

    @Inject
    @DataSource("consumed-events")
    AgroalDataSource consumedEventsDataSource;

    private static final String USERNAME_TO_CONNECT_WITH = "damdamdeo";
    private static final String USERNAME_PASSWORD = "damdamdeo";

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String keyCloakServerAuthUrl;

    @BeforeEach
    @Transactional
    public void setup() {
        try (final Connection con = secretStoreDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            stmt.executeUpdate("TRUNCATE TABLE SECRET_STORE");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (final Connection con = consumedEventsDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            stmt.executeUpdate("TRUNCATE TABLE CONSUMED_EVENT CASCADE");
            stmt.executeUpdate("TRUNCATE TABLE CONSUMED_EVENT_CONSUMER CASCADE");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        entityManager.createQuery("DELETE FROM TodoEntity").executeUpdate();

        entityManager.createNativeQuery("TRUNCATE TABLE todoentity_aud CASCADE").executeUpdate();
        entityManager.createNativeQuery("TRUNCATE TABLE revinfo CASCADE").executeUpdate();
        entityManager.createNativeQuery("ALTER SEQUENCE public.hibernate_sequence RESTART WITH 1");
    }

    @Test
    public void should_consume_todo_created_event_and_todo_marked_as_completed_event() throws Exception {
        // When
        final AggregateRootId aggregateRootId = new JacksonAggregateRootId("todoId", "TodoAggregateRoot");
        secretStore.store(aggregateRootId, "IbXcNPlTEnoPzWVPNwASmPepRVWBHhPN");
        kafkaDebeziumProducer.produce("todoCreatedEvent.json");
        await().atMost(2, TimeUnit.SECONDS).until(() ->
                given()
                    .auth().oauth2(getAccessToken())
                    .get("/todos/todoId")
                    .then().log().all()
                    .extract()
                    .statusCode() == 200
        );

        // Then
        given()
                .auth().oauth2(getAccessToken())
                .get("/todos/todoId")
                .then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("expected/todo.json"))
                .body("todoId", equalTo("todoId"))
                .body("description", equalTo("lorem ipsum"))
                .body("todoStatus", equalTo("IN_PROGRESS"))
                .body("canMarkTodoAsCompleted", equalTo(true))
                .body("version", equalTo(0));

        // When
        kafkaDebeziumProducer.produce("todoMarkedAsCompletedEvent.json");
        await().atMost(2, TimeUnit.SECONDS).until(() ->
                given()
                        .auth().oauth2(getAccessToken())
                        .get("/todos/todoId")
                        .then().log().all()
                        .extract()
                        .body().jsonPath().getInt("version") == 1
        );

        // Then
        given()
                .auth().oauth2(getAccessToken())
                .get("/todos/todoId")
                .then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("expected/todo.json"))
                .body("todoId", equalTo("todoId"))
                .body("description", equalTo("lorem ipsum"))
                .body("todoStatus", equalTo("COMPLETED"))
                .body("canMarkTodoAsCompleted", equalTo(false))
                .body("version", equalTo(1));

        given()
                .auth().oauth2(getAccessToken())
                .get("/todos")
                .then()
                .log().all()
                .statusCode(200)
                .body("$.size()", equalTo(1))
                .body("[0].todoId", equalTo("todoId"))
                .body("[0].description", equalTo("lorem ipsum"))
                .body("[0].todoStatus", equalTo("COMPLETED"))
                .body("[0].canMarkTodoAsCompleted", equalTo(false))
                .body("[0].version", equalTo(1));

        // Then Auditing
        final List<TodoEntity> todos = AuditReaderFactory.get(entityManager)
                .createQuery()
                .forRevisionsOfEntity(TodoEntity.class, true, true)
                .add(AuditEntity.id().eq("todoId"))
                .getResultList();

        assertTrue(EqualsBuilder.reflectionEquals(TodoEntity.newBuilder()
                .withTodoId("todoId")
                .withDescription("lorem ipsum")
                .withTodoStatus(TodoStatus.IN_PROGRESS)
                .withVersion(0l).build(), todos.get(0)));
        assertTrue(EqualsBuilder.reflectionEquals(TodoEntity.newBuilder()
                .withTodoId("todoId")
                .withDescription("lorem ipsum")
                .withTodoStatus(TodoStatus.COMPLETED)
                .withVersion(1l).build(), todos.get(1)));
    }

    private String getAccessToken() {
        return RestAssured
                .given()
                .param("grant_type", "password")
                .param("username", USERNAME_TO_CONNECT_WITH)
                .param("password", USERNAME_PASSWORD)
                .param("client_id", "todo-platform")
                .param("client_secret", "secret")
                .when()
                .post(keyCloakServerAuthUrl + "/protocol/openid-connect/token")
                .as(AccessTokenResponse.class).getToken();
    }

}
