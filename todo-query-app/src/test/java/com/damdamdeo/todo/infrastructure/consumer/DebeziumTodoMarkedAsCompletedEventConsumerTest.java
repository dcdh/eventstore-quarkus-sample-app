package com.damdamdeo.todo.infrastructure.consumer;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.Operation;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.DecryptedAggregateRootEventConsumable;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.record.event_in.DebeziumJsonbAggregateRootEventId;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.record.event_in.DebeziumJsonbAggregateRootId;
import com.damdamdeo.eventsourced.encryption.api.PresentSecret;
import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import com.damdamdeo.eventsourced.encryption.infra.jsonb.JsonObjectEncryptedAggregateRootId;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.todo.KafkaDebeziumProducer;
import com.damdamdeo.todo.domain.MarkTodoAsCompletedService;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.junit.mockito.InjectSpy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.json.Json;
import javax.transaction.Transactional;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class DebeziumTodoMarkedAsCompletedEventConsumerTest {

    @Inject
    KafkaDebeziumProducer kafkaDebeziumProducer;

    @InjectSpy
    TodoMarkedAsCompletedEventConsumer todoMarkedAsCompletedEventConsumer;

    @InjectMock
    MarkTodoAsCompletedService markTodoAsCompletedService;

    @InjectMock
    SecretStore secretStore;

    @Inject
    @DataSource("consumed-events")
    AgroalDataSource consumedEventsDataSource;

    @BeforeEach
    @Transactional
    public void flushDatabase() {
        try (final Connection con = consumedEventsDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            stmt.executeUpdate("TRUNCATE TABLE CONSUMED_EVENT CASCADE");
            stmt.executeUpdate("TRUNCATE TABLE CONSUMED_EVENT_CONSUMER CASCADE");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    public void setupSecretStore() {
        final AggregateRootId aggregateRootId = new JsonObjectEncryptedAggregateRootId("TodoAggregateRoot", "todoId");
        doReturn(new PresentSecret("IbXcNPlTEnoPzWVPNwASmPepRVWBHhPN"))
                .when(secretStore).read(aggregateRootId);
    }

    @AfterEach
    public void verifySecretStore() {
        verify(secretStore, atLeastOnce()).read(any());
    }

    @Test
    public void should_consume_event() throws Exception {
        // Given

        // When
        kafkaDebeziumProducer.produce("todoMarkedAsCompletedEvent.json");
        waitForEventToBeConsumed();

        // Then
        verify(todoMarkedAsCompletedEventConsumer, times(1)).consume(new DecryptedAggregateRootEventConsumable(
                new DebeziumJsonbAggregateRootEventId(new DebeziumJsonbAggregateRootId("TodoAggregateRoot", "todoId"), 1l),
                "TodoMarkedAsCompletedEvent",
                LocalDateTime.of(2019, Month.JULY, 12, 0, 7, 24, 922000000),
                Json.createReader(new StringReader("{\"todoId\":\"todoId\"}")).readObject(),
                Json.createReader(new StringReader("{\"user.anonymous\":false,\"user.name\":\"damdamdeo\"}")).readObject(),
                Json.createReader(new StringReader("{\"description\":\"lorem ipsum\",\"todoId\":\"todoId\",\"todoStatus\":\"COMPLETED\"}")).readObject()
        ), Operation.READ);

        verify(todoMarkedAsCompletedEventConsumer, atLeastOnce()).aggregateRootType();
        verify(todoMarkedAsCompletedEventConsumer, atLeastOnce()).eventType();
    }

    @Test
    public void should_call_mark_as_completed_service() throws Exception {
        // Given

        // When
        kafkaDebeziumProducer.produce("todoMarkedAsCompletedEvent.json");
        waitForEventToBeConsumed();

        // Then
        verify(markTodoAsCompletedService, times(1)).markTodoAsCompleted(
                "todoId",
                1l
        );
    }

    @Test
    public void should_consume_event_only_once() throws Exception {
        // Given
        kafkaDebeziumProducer.produce("todoMarkedAsCompletedEvent.json");
        waitForEventToBeConsumed();

        // When
        kafkaDebeziumProducer.produce("todoMarkedAsCompletedEvent.json");
        TimeUnit.SECONDS.sleep(2);// je n'ai pas de marqueur de fin d'execution...

        // Then
        verify(todoMarkedAsCompletedEventConsumer, times(1)).consume(any(), eq(Operation.READ));

        verify(todoMarkedAsCompletedEventConsumer, atLeastOnce()).aggregateRootType();
        verify(todoMarkedAsCompletedEventConsumer, atLeastOnce()).eventType();
    }

    private void waitForEventToBeConsumed() {
        await().atMost(2, TimeUnit.SECONDS)
                .until(() -> {
                    try (final Connection con = consumedEventsDataSource.getConnection();
                         final Statement stmt = con.createStatement();
                         final ResultSet resultSet = stmt.executeQuery("SELECT COUNT(*) AS count FROM CONSUMED_EVENT")) {
                        resultSet.next();
                        return resultSet.getLong("count") > 0;
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

}
