package com.damdamdeo.todo.aggregate.event;

import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventPayload;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.JacksonAggregateRootEventPayloadDeSerializer;
import io.quarkus.test.junit.QuarkusTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@QuarkusTest
public class TodoAggregateTodoMarkedAsCompletedEventPayloadTest {

    @Inject
    JacksonAggregateRootEventPayloadDeSerializer jacksonAggregateRootEventPayloadDeSerializer;

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(TodoAggregateTodoMarkedAsCompletedEventPayload.class).verify();
    }

    @Test
    public void should_serialize() {
        // Given
        final Secret secret = mock(Secret.class);

        // When
        final String serialized = jacksonAggregateRootEventPayloadDeSerializer.serialize(secret, new TodoAggregateTodoMarkedAsCompletedEventPayload("todoId"));

        // Then
        assertEquals("{\"@type\":\"TodoAggregateTodoMarkedAsCompletedEventPayload\",\"todoId\":\"todoId\"}", serialized);
        verify(secret, times(0)).encrypt(any(), any());
    }

    @Test
    public void should_deserialize() {
        // Given
        final Secret secret = mock(Secret.class);

        // When
        final AggregateRootEventPayload deserialized = jacksonAggregateRootEventPayloadDeSerializer.deserialize(secret,
                "{\"@type\":\"TodoAggregateTodoMarkedAsCompletedEventPayload\",\"todoId\":\"todoId\"}");

        // Then
        assertEquals(new TodoAggregateTodoMarkedAsCompletedEventPayload("todoId"), deserialized);
        verify(secret, times(0)).decrypt(any(), any());
    }

}
