package com.damdamdeo.todo;

import com.jayway.restassured.module.jsv.JsonSchemaValidator;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

@QuarkusTest
public class EventStoreEventConsumerTest {

    @Inject
    KafkaDebeziumProducer kafkaDebeziumProducer;

    @Inject
    EntityManager em;

    @BeforeEach
    @Transactional
    public void setup() {
        em.createQuery("DELETE FROM TodoEntity").executeUpdate();
        em.createQuery("DELETE FROM KafkaEventEntity").executeUpdate();
    }

    @Test
    public void should_consume_todo_created_event_and_todo_marked_as_completed_event() throws Exception {
        // When
        kafkaDebeziumProducer.produce("TodoCreatedEvent.json");
        Thread.sleep(1000);

        // Then
        given()
                .get("/todos/todoId")
                .then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("expected/todo.json"))
                .body("todoId", equalTo("todoId"))
                .body("description", equalTo("lorem ipsum"))
                .body("todoStatus", equalTo("IN_PROGRESS"))
                .body("version", equalTo(0));

        // When
        kafkaDebeziumProducer.produce("TodoMarkedAsCompletedEvent.json");
        Thread.sleep(1000);

        // Then
        given()
                .get("/todos/todoId")
                .then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("expected/todo.json"))
                .body("todoId", equalTo("todoId"))
                .body("description", equalTo("lorem ipsum"))
                .body("todoStatus", equalTo("COMPLETED"))
                .body("version", equalTo(1));
    }

}
