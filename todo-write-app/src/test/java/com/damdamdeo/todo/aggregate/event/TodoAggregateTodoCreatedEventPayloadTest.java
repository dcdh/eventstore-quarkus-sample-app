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
public class TodoAggregateTodoCreatedEventPayloadTest {

    @Inject
    JacksonAggregateRootEventPayloadDeSerializer jacksonAggregateRootEventPayloadDeSerializer;

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(TodoAggregateTodoCreatedEventPayload.class).verify();
    }

    @Test
    public void should_serialize() {
        // Given
        final AggregateRootSecret aggregateRootSecret = mock(AggregateRootSecret.class);
        doReturn("AAlwSnNqyIRebwRqBfHufaCTXoRFRllg").when(aggregateRootSecret).secret();

        // When
        final String serialized = jacksonAggregateRootEventPayloadDeSerializer.serialize(Optional.of(aggregateRootSecret), new TodoAggregateTodoCreatedEventPayload("todoId", "lorem ipsum"));

        // Then
        assertEquals("{\"@type\":\"TodoAggregateTodoCreatedEventPayload\",\"todoId\":\"todoId\",\"description\":\"uWtQHOtmgpaw22nCiexwpg==\"}", serialized);
        verify(aggregateRootSecret, times(1)).secret();
    }

    @Test
    public void should_deserialize() {
        // Given
        final AggregateRootSecret aggregateRootSecret = mock(AggregateRootSecret.class);
        doReturn("AAlwSnNqyIRebwRqBfHufaCTXoRFRllg").when(aggregateRootSecret).secret();

        // When
        final AggregateRootEventPayload aggregateRootEventPayload = jacksonAggregateRootEventPayloadDeSerializer.deserialize(Optional.of(aggregateRootSecret),
                "{\"@type\":\"TodoAggregateTodoCreatedEventPayload\",\"todoId\":\"todoId\",\"description\":\"uWtQHOtmgpaw22nCiexwpg==\"}");

        // Then
        assertEquals(new TodoAggregateTodoCreatedEventPayload("todoId", "lorem ipsum"), aggregateRootEventPayload);
        verify(aggregateRootSecret, times(1)).secret();
    }

}
