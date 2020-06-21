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
import static org.mockito.Mockito.doReturn;

@QuarkusTest
public class TodoAggregateTodoCreatedEventPayloadConsumerTest {

    @Inject
    JacksonAggregateRootEventPayloadConsumerDeserializer jacksonAggregateRootEventPayloadConsumerDeserializer;

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(TodoAggregateTodoCreatedEventPayloadConsumer.class).verify();
    }

    @Test
    public void should_deserialize() {
        // Given
        final AggregateRootSecret aggregateRootSecret = mock(AggregateRootSecret.class);
        doReturn("AAlwSnNqyIRebwRqBfHufaCTXoRFRllg").when(aggregateRootSecret).secret();

        // When
        final AggregateRootEventPayloadConsumer deserialized = jacksonAggregateRootEventPayloadConsumerDeserializer.deserialize(Optional.of(aggregateRootSecret),
                "{\"@type\":\"TodoAggregateTodoCreatedEventPayload\",\"todoId\":\"todoId\",\"description\":\"uWtQHOtmgpaw22nCiexwpg==\"}");

        // Then
        assertEquals(new TodoAggregateTodoCreatedEventPayloadConsumer("todoId", "lorem ipsum"), deserialized);
        verify(aggregateRootSecret, times(1)).secret();
    }

}
