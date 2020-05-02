package com.damdamdeo.todo;

import com.damdamdeo.eventdataspreader.eventsourcing.api.SecretStore;
import com.damdamdeo.todo.domain.api.TodoStatus;
import com.damdamdeo.todo.infrastructure.TodoEntity;
import com.jayway.restassured.module.jsv.JsonSchemaValidator;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.test.junit.QuarkusTest;
import org.hamcrest.Matchers;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.core.MediaType;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class EventStoreEventConsumerTest {

    @Inject
    KafkaDebeziumProducer kafkaDebeziumProducer;

    @Inject
    EntityManager entityManager;

    @Inject
    SecretStore secretStore;

    @Inject
    @DataSource("secret-store")
    AgroalDataSource secretStoreDataSource;

    @BeforeEach
    @Transactional
    public void setup() {
        try (final Connection con = secretStoreDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            stmt.executeUpdate("TRUNCATE TABLE SecretStore");
        } catch (SQLException e) {
            // Do not throw an exception as the table is not present because the @PostConstruct in AgroalDataSourceSecretStore
            // has not be called yet... bug ?!?
            // throw new RuntimeException(e);
        }

        entityManager.createQuery("DELETE FROM TodoEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM EventConsumerConsumedEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM EventConsumedEntity").executeUpdate();

        entityManager.createNativeQuery("DELETE FROM todoentity_aud").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM revinfo").executeUpdate();
        entityManager.createNativeQuery("ALTER SEQUENCE public.hibernate_sequence RESTART WITH 1");
    }

    @Test
    public void should_consume_todo_created_event_and_todo_marked_as_completed_event() throws Exception {
        // When
        secretStore.store("TodoAggregateRoot", "todoId", "AAlwSnNqyIRebwRqBfHufaCTXoRFRllg");
        kafkaDebeziumProducer.produce("TodoCreatedEvent.json");
        await().atMost(100, TimeUnit.SECONDS).until(() ->
                given()
                    .get("/todos/todoId")
                    .then().log().all()
                    .extract()
                    .statusCode() == 200
        );

        // Then
        given()
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
        kafkaDebeziumProducer.produce("TodoMarkedAsCompletedEvent.json");
        await().atMost(10, TimeUnit.SECONDS).until(() ->
                given()
                        .get("/todos/todoId")
                        .then().log().all()
                        .extract()
                        .body().jsonPath().getInt("version") == 1
        );

        // Then
        given()
                .get("/todos/todoId")
                .then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("expected/todo.json"))
                .body("todoId", equalTo("todoId"))
                .body("description", equalTo("lorem ipsum"))
                .body("todoStatus", equalTo("COMPLETED"))
                .body("canMarkTodoAsCompleted", equalTo(false))
                .body("version", equalTo(1));

        // Then Auditing
        final List<TodoEntity> todos = AuditReaderFactory.get(entityManager)
                .createQuery()
                .forRevisionsOfEntity(TodoEntity.class, true, true)
                .add(AuditEntity.id().eq("todoId"))
                .getResultList();

        assertEquals(2, todos.size());
        assertThat(todos).containsExactly(new TodoEntity("todoId", "lorem ipsum", TodoStatus.IN_PROGRESS, 0l),
                new TodoEntity("todoId", "lorem ipsum", TodoStatus.COMPLETED, 1l));
    }

    @Test
    public void should_api_fail_when_retrieving_unknown_todo() {
        given()
                .when()
                .get("/todos/todoId")
                .then()
                .statusCode(404)
                .contentType(MediaType.TEXT_PLAIN)
                .body(Matchers.equalTo("Le todoId 'todoId' est inconnu."));
    }
}
