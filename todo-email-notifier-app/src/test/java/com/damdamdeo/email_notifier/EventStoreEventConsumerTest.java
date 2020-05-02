package com.damdamdeo.email_notifier;

import com.damdamdeo.email_notifier.domain.EmailNotifier;
import com.damdamdeo.email_notifier.domain.TodoStatus;
import com.damdamdeo.email_notifier.infrastructure.TodoEntity;
import com.damdamdeo.eventdataspreader.eventsourcing.api.SecretStore;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.test.junit.QuarkusTest;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
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
    EntityManager entityManager;

    @Inject
    EmailNotifier emailNotifier;

    @Inject
    SecretStore secretStore;

    @Inject
    @DataSource("secret-store")
    AgroalDataSource secretStoreDataSource;

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
        secretStore.store("TodoAggregateRoot", "todoId", "AAlwSnNqyIRebwRqBfHufaCTXoRFRllg");
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
        final List<TodoEntity> todos = AuditReaderFactory.get(entityManager)
                .createQuery()
                .forRevisionsOfEntity(TodoEntity.class, true, true)
                .add(AuditEntity.id().eq("todoId"))
                .getResultList();

        assertEquals(2, todos.size());
        assertThat(todos).containsExactly(new TodoEntity("todoId", "lorem ipsum", TodoStatus.IN_PROGRESS, 0l),
                new TodoEntity("todoId", "lorem ipsum", TodoStatus.COMPLETED, 1l));

    }

}
