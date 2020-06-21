package com.damdamdeo.email_notifier.consumer.event;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventPayloadConsumer;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.JacksonAggregateRootEventPayloadConsumerDeserializer;
import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;
import io.quarkus.test.junit.QuarkusTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@QuarkusTest
public class TodoAggregateTodoMarkedAsCompletedEventPayloadConsumerTest {

    @Inject
    JacksonAggregateRootEventPayloadConsumerDeserializer jacksonAggregateRootEventPayloadConsumerDeserializer;

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(TodoAggregateTodoMarkedAsCompletedEventPayloadConsumer.class).verify();
    }

    @Test
    public void should_deserialize() {
        // Given
        final AggregateRootSecret aggregateRootSecret = mock(AggregateRootSecret.class);

        // When
        final AggregateRootEventPayloadConsumer deserialized = jacksonAggregateRootEventPayloadConsumerDeserializer.deserialize(Optional.of(aggregateRootSecret),
                "{\"@type\":\"TodoAggregateTodoMarkedAsCompletedEventPayload\",\"todoId\":\"todoId\"}");

        // Then
        assertEquals(new TodoAggregateTodoMarkedAsCompletedEventPayloadConsumer("todoId"), deserialized);
        verify(aggregateRootSecret, times(0)).secret();
    }

}
