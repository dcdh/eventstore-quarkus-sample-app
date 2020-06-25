package com.damdamdeo.email_notifier.consumer;

import com.damdamdeo.email_notifier.KafkaDebeziumProducer;
import com.damdamdeo.email_notifier.consumer.event.TodoAggregateTodoCreatedEventPayloadConsumer;
import com.damdamdeo.email_notifier.domain.TodoStatus;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.DebeziumAggregateRootEventId;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.DecryptedAggregateRootEventConsumable;
import com.damdamdeo.eventsourced.encryption.api.PresentSecret;
import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
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

    @Test
    public void should_consume_event() throws Exception {
        // Given
        doCallRealMethod().when(todoCreatedEventConsumer).aggregateRootType();
        doCallRealMethod().when(todoCreatedEventConsumer).eventType();
        doReturn(new PresentSecret("AAlwSnNqyIRebwRqBfHufaCTXoRFRllg"))
                .when(secretStore).read("TodoAggregateRoot", "todoId");

        // When
        kafkaDebeziumProducer.produce("TodoCreatedEvent.json");
        waitForEventToBeConsumed();

        // Then
        verify(todoCreatedEventConsumer, times(1)).consume(new DecryptedAggregateRootEventConsumable(
                new DebeziumAggregateRootEventId("todoId", "TodoAggregateRoot", 0l),
                "TodoCreatedEvent",
                LocalDateTime.of(2019, Month.JULY, 12, 0, 7, 24, 742000000),
                new TodoAggregateTodoCreatedEventPayloadConsumer("todoId", "lorem ipsum"),
                new DefaultEventMetadataConsumer(),
                new TodoAggregateRootMaterializedStateConsumer("todoId", "TodoAggregateRoot", 0L, "lorem ipsum", TodoStatus.IN_PROGRESS)
        ));

        verify(todoCreatedEventConsumer, times(1)).aggregateRootType();
        verify(todoCreatedEventConsumer, times(1)).eventType();
        verify(secretStore, times(1)).read(any(), any());
        verifyNoMoreInteractions(todoCreatedEventConsumer, secretStore);
    }

    @Test
    public void should_consume_event_only_once() throws Exception {
        // Given
        doCallRealMethod().when(todoCreatedEventConsumer).aggregateRootType();
        doCallRealMethod().when(todoCreatedEventConsumer).eventType();
        doReturn(new PresentSecret("AAlwSnNqyIRebwRqBfHufaCTXoRFRllg"))
                .when(secretStore).read("TodoAggregateRoot", "todoId");
        kafkaDebeziumProducer.produce("TodoCreatedEvent.json");
        waitForEventToBeConsumed();

        // When
        kafkaDebeziumProducer.produce("TodoCreatedEvent.json");
        TimeUnit.SECONDS.sleep(2);// je n'ai pas de marqueur de fin d'execution...

        // Then
        verify(todoCreatedEventConsumer, times(1)).consume(any());

        verify(todoCreatedEventConsumer, times(1)).aggregateRootType();
        verify(todoCreatedEventConsumer, times(1)).eventType();
        verify(secretStore, atLeastOnce()).read(any(), any());
        verifyNoMoreInteractions(todoCreatedEventConsumer, secretStore);
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
