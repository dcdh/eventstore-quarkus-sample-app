package com.damdamdeo.todo;

import com.damdamdeo.todo.domain.TodoStatus;
import com.damdamdeo.todo.infrastructure.TodoEntity;
import com.jayway.restassured.module.jsv.JsonSchemaValidator;
import io.quarkus.test.junit.QuarkusTest;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

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
    EntityManager em;

    @BeforeEach
    @Transactional
    public void setup() {
        em.createQuery("DELETE FROM TodoEntity").executeUpdate();
        em.createQuery("DELETE FROM EventConsumerConsumedEntity").executeUpdate();
        em.createQuery("DELETE FROM EventConsumedEntity").executeUpdate();

        em.createNativeQuery("DELETE FROM todoentity_aud").executeUpdate();
        em.createNativeQuery("DELETE FROM revinfo").executeUpdate();
        em.createNativeQuery("ALTER SEQUENCE public.hibernate_sequence RESTART WITH 1");
    }

    @Test
    public void should_consume_todo_created_event_and_todo_marked_as_completed_event() throws Exception {
        // When
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
                .body("version", equalTo(1));

        // Then Auditing
        final List<TodoEntity> todos = AuditReaderFactory.get(em)
                .createQuery()
                .forRevisionsOfEntity(TodoEntity.class, true, true)
                .add(AuditEntity.id().eq("todoId"))
                .getResultList();

        assertEquals(2, todos.size());
        assertThat(todos).containsExactly(new TodoEntity("todoId", "lorem ipsum", TodoStatus.IN_PROGRESS, "873ecba4-3f2e-4663-b9f1-b912e17bfc9b", 0l),
                new TodoEntity("todoId", "lorem ipsum", TodoStatus.COMPLETED, "27f243d6-ba3a-468f-8435-4537e86ae64b", 1l));
    }

}
