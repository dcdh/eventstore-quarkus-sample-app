package com.damdamdeo.email_notifier;

import com.damdamdeo.email_notifier.domain.EmailNotifier;
import com.damdamdeo.email_notifier.domain.TodoStatus;
import com.damdamdeo.email_notifier.infrastructure.TodoEntity;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.builder.RequestSpecBuilder;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.util.List;
import java.util.concurrent.CompletionStage;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
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
    @Transactional
    public void setup() {
        given(new RequestSpecBuilder().setBaseUri("http://localhost").setPort(8025).build())
                .when()
                .delete("/api/v1/messages")
                .then()
                .statusCode(200);
        em.createQuery("DELETE FROM TodoEntity").executeUpdate();
        em.createQuery("DELETE FROM KafkaEventEntity").executeUpdate();

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
        given(new RequestSpecBuilder().setBaseUri("http://localhost").setPort(8025).build())
                .when()
                .get("/api/v1/messages")
                .then()
                .statusCode(200)
                .body("[0].Content.Headers.Subject[0]", equalTo("New Todo created"))
                .body("[0].MIME.Parts[0].MIME.Parts[0].Body", containsString("lorem ipsum"));

        // When
        kafkaDebeziumProducer.produce("TodoMarkedAsCompletedEvent.json");
        Thread.sleep(1000);

        // Then
        given(new RequestSpecBuilder().setBaseUri("http://localhost").setPort(8025).build())
                .when()
                .get("/api/v1/messages")
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
