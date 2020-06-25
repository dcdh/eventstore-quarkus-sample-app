package com.damdamdeo.todo.consumer.event;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventPayloadConsumer;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.JacksonAggregateRootEventPayloadConsumerDeserializer;
import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.Secret;
import io.quarkus.test.junit.QuarkusTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@QuarkusTest
public class TodoAggregateTodoCreatedEventPayloadConsumerTest {

    @Inject
    JacksonAggregateRootEventPayloadConsumerDeserializer jacksonAggregateRootEventPayloadConsumerDeserializer;

    @Inject
    Encryption encryption;

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(TodoAggregateTodoCreatedEventPayloadConsumer.class).verify();
    }

    @Test
    public void should_deserialize() {
        // Given
        final Secret secret = mock(Secret.class);
        doReturn("lorem ipsum").when(secret).decrypt("uWtQHOtmgpaw22nCiexwpg==", encryption);

        // When
        final AggregateRootEventPayloadConsumer deserialized = jacksonAggregateRootEventPayloadConsumerDeserializer.deserialize(secret,
                "{\"@type\":\"TodoAggregateTodoCreatedEventPayload\",\"todoId\":\"todoId\",\"description\":\"uWtQHOtmgpaw22nCiexwpg==\"}");

        // Then
        assertEquals(new TodoAggregateTodoCreatedEventPayloadConsumer("todoId", "lorem ipsum"), deserialized);
        verify(secret, times(1)).decrypt(any(), any());
    }

}
