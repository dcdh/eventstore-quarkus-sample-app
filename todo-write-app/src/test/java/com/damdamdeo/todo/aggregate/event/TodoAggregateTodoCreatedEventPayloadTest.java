package com.damdamdeo.todo.aggregate.event;

import com.damdamdeo.eventsourced.encryption.api.Encryption;
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
public class TodoAggregateTodoCreatedEventPayloadTest {

    @Inject
    JacksonAggregateRootEventPayloadDeSerializer jacksonAggregateRootEventPayloadDeSerializer;

    @Inject
    Encryption encryption;

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(TodoAggregateTodoCreatedEventPayload.class).verify();
    }

    @Test
    public void should_serialize() {
        // Given
        final Secret secret = mock(Secret.class);
        doReturn("uWtQHOtmgpaw22nCiexwpg==").when(secret).encrypt("lorem ipsum", encryption);

        // When
        final String serialized = jacksonAggregateRootEventPayloadDeSerializer.serialize(secret, new TodoAggregateTodoCreatedEventPayload("todoId", "lorem ipsum"));

        // Then
        assertEquals("{\"@type\":\"TodoAggregateTodoCreatedEventPayload\",\"todoId\":\"todoId\",\"description\":\"uWtQHOtmgpaw22nCiexwpg==\"}", serialized);
        verify(secret, times(1)).encrypt(any(), any());
    }

    @Test
    public void should_deserialize() {
        // Given
        final Secret secret = mock(Secret.class);
        doReturn("lorem ipsum").when(secret).decrypt("uWtQHOtmgpaw22nCiexwpg==", encryption);

        // When
        final AggregateRootEventPayload aggregateRootEventPayload = jacksonAggregateRootEventPayloadDeSerializer.deserialize(secret,
                "{\"@type\":\"TodoAggregateTodoCreatedEventPayload\",\"todoId\":\"todoId\",\"description\":\"uWtQHOtmgpaw22nCiexwpg==\"}");

        // Then
        assertEquals(new TodoAggregateTodoCreatedEventPayload("todoId", "lorem ipsum"), aggregateRootEventPayload);
        verify(secret, times(1)).decrypt(any(), any());
    }

}
