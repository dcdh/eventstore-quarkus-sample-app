package com.damdamdeo.email_notifier;

import com.damdamdeo.email_notifier.domain.EmailNotifier;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.builder.RequestSpecBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
public class EventStoreEventConsumerTest {

    @Inject
    KafkaDebeziumProducer kafkaDebeziumProducer;

    @Inject
    EntityManager em;

    @Inject
    EmailNotifier emailNotifier;

    @BeforeEach
    @Transactional
    public void setup() {
        given(new RequestSpecBuilder().setBaseUri("http://localhost").setPort(8025).build())
                .when()
                .delete("/api/v1/messages")
                .then()
                .statusCode(200);
        em.createQuery("DELETE FROM TodoEntity").executeUpdate();
        em.createQuery("DELETE FROM KafkaEventEntity").executeUpdate();
    }

    @Test
    public void should_send_email() {
        // Given
        emailNotifier.notify("content", "subject");

        // Then
        given(new RequestSpecBuilder().setBaseUri("http://localhost").setPort(8025).build())
                .when()
                .get("/api/v1/messages")
                .then()
                .statusCode(200)
                .body("[0].Content.Headers.Subject[0]", equalTo("subject"))
                .body("[0].MIME.Parts[0].MIME.Parts[0].Body", equalTo("content"));
    }

    @Test
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
//        kafkaDebeziumProducer.produce("TodoMarkedAsCompletedEvent.json");
//        Thread.sleep(1000);

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
