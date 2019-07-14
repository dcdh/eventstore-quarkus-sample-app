package com.damdamdeo.email_notifier;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

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
//l'email en localhost ne fonctionne pas ... passer par l'api specifqiue fourni par quarkus !!!
//    Quoi que non car en test e2e cela ne marchera pas ...
//    @Test
    public void should_consume_todo_created_event_and_todo_marked_as_completed_event() throws Exception {
        // When
        kafkaDebeziumProducer.produce("TodoCreatedEvent.json");
        Thread.sleep(1000);

        // Then
//        given()
//                .get("/todos/todoId")
//                .then()
//                .statusCode(200)
//                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("expected/todo.json"))
//                .body("todoId", equalTo("todoId"))
//                .body("description", equalTo("lorem ipsum"))
//                .body("todoStatus", equalTo("IN_PROGRESS"))
//                .body("version", equalTo(0));

        // When
        kafkaDebeziumProducer.produce("TodoMarkedAsCompletedEvent.json");
        Thread.sleep(1000);

        // Then
//        given()
//                .get("/todos/todoId")
//                .then()
//                .statusCode(200)
//                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("expected/todo.json"))
//                .body("todoId", equalTo("todoId"))
//                .body("description", equalTo("lorem ipsum"))
//                .body("todoStatus", equalTo("COMPLETED"))
//                .body("version", equalTo(1));
    }

}
