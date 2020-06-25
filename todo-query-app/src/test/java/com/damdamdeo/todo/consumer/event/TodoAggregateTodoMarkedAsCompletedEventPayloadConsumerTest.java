package com.damdamdeo.todo.consumer.event;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventPayloadConsumer;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.JacksonAggregateRootEventPayloadConsumerDeserializer;
import com.damdamdeo.eventsourced.encryption.api.Secret;
import io.quarkus.test.junit.QuarkusTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

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
        final Secret secret = mock(Secret.class);

        // When
        final AggregateRootEventPayloadConsumer deserialized = jacksonAggregateRootEventPayloadConsumerDeserializer.deserialize(secret,
                "{\"@type\":\"TodoAggregateTodoMarkedAsCompletedEventPayload\",\"todoId\":\"todoId\"}");

        // Then
        assertEquals(new TodoAggregateTodoMarkedAsCompletedEventPayloadConsumer("todoId"), deserialized);
        verify(secret, times(0)).secret();
    }

}
