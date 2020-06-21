package com.damdamdeo.todo;

import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import com.damdamdeo.todo.domain.api.TodoStatus;
import com.damdamdeo.todo.infrastructure.TodoEntity;
import com.jayway.restassured.module.jsv.JsonSchemaValidator;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.junit.jupiter.api.*;

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
        secretStore.store("TodoAggregateRoot", "todoId", "AAlwSnNqyIRebwRqBfHufaCTXoRFRllg");
        kafkaDebeziumProducer.produce("TodoCreatedEvent.json");
        await().atMost(10, TimeUnit.SECONDS).until(() ->
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

        given()
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

        assertTrue(EqualsBuilder.reflectionEquals(new TodoEntity("todoId", "lorem ipsum", TodoStatus.IN_PROGRESS, 0l), todos.get(0)));
        assertTrue(EqualsBuilder.reflectionEquals(new TodoEntity("todoId", "lorem ipsum", TodoStatus.COMPLETED, 1l), todos.get(1)));
    }

}
