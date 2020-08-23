package com.damdamdeo.email_notifier.consumer;

import com.damdamdeo.email_notifier.KafkaDebeziumProducer;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.DebeziumAggregateRootEventId;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.DecryptedAggregateRootEventConsumable;
import com.damdamdeo.eventsourced.encryption.api.PresentSecret;
import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import com.damdamdeo.eventsourced.encryption.infra.jackson.JacksonAggregateRootId;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.transaction.Transactional;
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
public class DebeziumTodoCreatedEventConsumerTest {

    @Inject
    KafkaDebeziumProducer kafkaDebeziumProducer;

    @InjectMock
    TodoCreatedEventConsumer todoCreatedEventConsumer;

    @InjectMock
    SecretStore secretStore;

    @Inject
    @DataSource("consumed-events")
    AgroalDataSource consumedEventsDataSource;

    @BeforeEach
    @Transactional
    public void setup() {
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
        final AggregateRootId aggregateRootId = new JacksonAggregateRootId("todoId", "TodoAggregateRoot");
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
        final ObjectMapper objectMapper = new ObjectMapper();
        doCallRealMethod().when(todoCreatedEventConsumer).aggregateRootType();
        doCallRealMethod().when(todoCreatedEventConsumer).eventType();

        // When
        kafkaDebeziumProducer.produce("todoCreatedEvent.json");
        waitForEventToBeConsumed();

        // Then
        verify(todoCreatedEventConsumer, times(1)).consume(new DecryptedAggregateRootEventConsumable(
                new DebeziumAggregateRootEventId("todoId", "TodoAggregateRoot", 0l),
                "TodoCreatedEvent",
                LocalDateTime.of(2019, Month.JULY, 12, 0, 7, 24, 742000000),
                objectMapper.readTree("{\"todoId\":\"todoId\",\"description\":\"lorem ipsum\"}"),
                objectMapper.readTree("{\"user.anonymous\":false,\"user.name\":\"damdamdeo\"}"),
                objectMapper.readTree("{\"description\":\"lorem ipsum\",\"todoId\":\"todoId\",\"todoStatus\":\"IN_PROGRESS\"}")
        ));

        verify(todoCreatedEventConsumer, times(1)).aggregateRootType();
        verify(todoCreatedEventConsumer, times(1)).eventType();
    }

    @Test
    public void should_consume_event_only_once() throws Exception {
        // Given
        doCallRealMethod().when(todoCreatedEventConsumer).aggregateRootType();
        doCallRealMethod().when(todoCreatedEventConsumer).eventType();
        kafkaDebeziumProducer.produce("todoCreatedEvent.json");
        waitForEventToBeConsumed();

        // When
        kafkaDebeziumProducer.produce("todoCreatedEvent.json");
        TimeUnit.SECONDS.sleep(2);// je n'ai pas de marqueur de fin d'execution...

        // Then
        verify(todoCreatedEventConsumer, times(1)).consume(any());

        verify(todoCreatedEventConsumer, times(1)).aggregateRootType();
        verify(todoCreatedEventConsumer, times(1)).eventType();
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
