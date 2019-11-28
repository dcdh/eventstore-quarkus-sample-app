package com.damdamdeo.email_notifier;

import com.damdamdeo.email_notifier.domain.EmailNotifier;
import com.damdamdeo.email_notifier.domain.TodoStatus;
import com.damdamdeo.email_notifier.infrastructure.TodoEntity;
import io.quarkus.test.junit.QuarkusTest;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class EventStoreEventConsumerTest {

    @Inject
    KafkaDebeziumProducer kafkaDebeziumProducer;

    @Inject
    EntityManager em;

    @Inject
    EmailNotifier emailNotifier;

    @BeforeEach
    public void cleanMessages() {
        given()
                .when()
                .delete("http://localhost:8025/api/v1/messages")
                .then()
                .statusCode(200);
    }

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
    public void should_send_email() throws Exception {
        // Given
        final CompletionStage<Void> completionStage = emailNotifier.notify("subject", "content");
        completionStage.toCompletableFuture().get();

        // Then
        given()
                .when()
                .get("http://localhost:8025/api/v1/messages")
                .then()
                .statusCode(200)
                .body("[0].Content.Headers.Subject[0]", equalTo("subject"))
                .body("[0].MIME.Parts[0].MIME.Parts[0].Body", equalTo("content"));
    }

    @Test
    public void should_consume_todo_created_event_and_todo_marked_as_completed_event() throws Exception {
        // When
        kafkaDebeziumProducer.produce("TodoCreatedEvent.json");
        await().atMost(10, TimeUnit.SECONDS).until(() ->
                given()
                        .when()
                        .get("http://localhost:8025/api/v1/messages")
                        .then()
                        .log().all()
                        .statusCode(200)
                        .extract()
                        .body()
                        .jsonPath().getList("$").size() == 1);

        // Then
        given()
                .when()
                .get("http://localhost:8025/api/v1/messages")
                .then()
                .log().all()
                .statusCode(200)
                .body("[0].Content.Headers.Subject[0]", equalTo("New Todo created"))
                .body("[0].MIME.Parts[0].MIME.Parts[0].Body", containsString("lorem ipsum"));

        // When
        kafkaDebeziumProducer.produce("TodoMarkedAsCompletedEvent.json");
        await().atMost(10, TimeUnit.SECONDS).until(() ->
                given()
                        .when()
                        .get("http://localhost:8025/api/v1/messages")
                        .then()
                        .log().all()
                        .statusCode(200)
                        .extract()
                        .body()
                        .jsonPath().getList("$").size() == 2);

        // Then
        given()
                .when()
                .get("http://localhost:8025/api/v1/messages")
                .then()
                .statusCode(200)
                .body("[0].Content.Headers.Subject[0]", equalTo("Todo marked as completed"))
                .body("[0].MIME.Parts[0].MIME.Parts[0].Body", containsString("lorem ipsum"));

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
