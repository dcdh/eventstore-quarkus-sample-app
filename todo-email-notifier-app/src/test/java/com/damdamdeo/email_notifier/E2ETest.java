package com.damdamdeo.email_notifier;

import com.damdamdeo.email_notifier.domain.EmailNotifier;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.record.event_in.DebeziumJsonbAggregateRootId;
import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.*;

@QuarkusTest
public class E2ETest {

    @Inject
    KafkaDebeziumProducer kafkaDebeziumProducer;

    @Inject
    EmailNotifier emailNotifier;

    @Inject
    SecretStore secretStore;

    @Inject
    @DataSource("secret-store")
    AgroalDataSource secretStoreDataSource;

    @Inject
    @DataSource("consumed-events")
    AgroalDataSource consumedEventsDataSource;

    @ConfigProperty(name = "quarkus.mailer.api.port")
    String quarkusMailerApiPort;

    @BeforeEach
    public void cleanMessages() {
        given()
                .when()
                .delete("http://localhost:" + quarkusMailerApiPort + "/api/v1/messages")
                .then()
                .statusCode(200);
    }

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
    }

    @Test
    public void should_send_email() throws Exception {
        // Given
        emailNotifier.notify("subject", "content");

        // Then
        given()
                .when()
                .get("http://localhost:" + quarkusMailerApiPort + "/api/v1/messages")
                .then()
                .statusCode(200)
                .body("[0].Content.Headers.Subject[0]", equalTo("subject"))
                .body("[0].MIME.Parts[0].MIME.Parts[0].Body", equalTo("content"));
    }

    @Test
    public void should_consume_todo_created_event_and_todo_marked_as_completed_event() throws Exception {
        // When
        final AggregateRootId aggregateRootId = new DebeziumJsonbAggregateRootId("TodoAggregateRoot", "todoId");
        secretStore.store(aggregateRootId, "IbXcNPlTEnoPzWVPNwASmPepRVWBHhPN");
        kafkaDebeziumProducer.produce("todoCreatedEvent.json");
        await().atMost(2, TimeUnit.SECONDS).until(() ->
                given()
                        .when()
                        .get("http://localhost:" + quarkusMailerApiPort + "/api/v1/messages")
                        .then()
                        .log().ifValidationFails()
                        .statusCode(200)
                        .extract()
                        .body()
                        .jsonPath().getList("$").size() == 1);

        // Then
        given()
                .when()
                .get("http://localhost:" + quarkusMailerApiPort + "/api/v1/messages")
                .then()
                .log().all()
                .statusCode(200)
                .body("[0].Content.Headers.Subject[0]", equalTo("New Todo created"))
                .body("[0].MIME.Parts[0].MIME.Parts[0].Body", containsString("lorem ipsum"));

        // When
        kafkaDebeziumProducer.produce("todoMarkedAsCompletedEvent.json");
        await().atMost(2, TimeUnit.SECONDS).until(() ->
                given()
                        .when()
                        .get("http://localhost:" + quarkusMailerApiPort + "/api/v1/messages")
                        .then()
                        .log().ifValidationFails()
                        .statusCode(200)
                        .extract()
                        .body()
                        .jsonPath().getList("$").size() == 2);

        // Then
        given()
                .when()
                .get("http://localhost:" + quarkusMailerApiPort + "/api/v1/messages")
                .then()
                .statusCode(200)
                .body("[0].Content.Headers.Subject[0]", equalTo("Todo marked as completed"))
                .body("[0].MIME.Parts[0].MIME.Parts[0].Body", containsString("lorem ipsum"));
    }

}
