package com.damdamdeo.todo.aggregate.event;

import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventPayload;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.JacksonAggregateRootEventPayloadDeSerializer;
import io.quarkus.test.junit.QuarkusTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Optional;

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
        final AggregateRootSecret aggregateRootSecret = mock(AggregateRootSecret.class);

        // When
        final String serialized = jacksonAggregateRootEventPayloadDeSerializer.serialize(Optional.of(aggregateRootSecret), new TodoAggregateTodoMarkedAsCompletedEventPayload("todoId"));

        // Then
        assertEquals("{\"@type\":\"TodoAggregateTodoMarkedAsCompletedEventPayload\",\"todoId\":\"todoId\"}", serialized);
        verify(aggregateRootSecret, times(0)).secret();
    }

    @Test
    public void should_deserialize() {
        // Given
        final AggregateRootSecret aggregateRootSecret = mock(AggregateRootSecret.class);

        // When
        final AggregateRootEventPayload deserialized = jacksonAggregateRootEventPayloadDeSerializer.deserialize(Optional.of(aggregateRootSecret),
                "{\"@type\":\"TodoAggregateTodoMarkedAsCompletedEventPayload\",\"todoId\":\"todoId\"}");

        // Then
        assertEquals(new TodoAggregateTodoMarkedAsCompletedEventPayload("todoId"), deserialized);
        verify(aggregateRootSecret, times(0)).secret();
    }

}
