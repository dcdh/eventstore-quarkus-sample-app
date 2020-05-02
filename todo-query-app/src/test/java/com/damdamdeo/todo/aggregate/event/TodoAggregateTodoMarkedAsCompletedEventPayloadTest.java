package com.damdamdeo.todo.aggregate.event;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventPayload;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventPayloadDeserializer;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.JacksonEventPayloadDeserializer;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.spi.JacksonEventPayloadSubtypes;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.spi.JacksonSubtype;
import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TodoAggregateTodoMarkedAsCompletedEventPayloadTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(TodoAggregateTodoMarkedAsCompletedEventPayload.class).verify();
    }

    private static class DefaultJacksonEventPayloadSubtypes implements JacksonEventPayloadSubtypes {

        @Override
        public List<JacksonSubtype<EventPayload>> jacksonSubtypes() {
            return singletonList(new JacksonSubtype<>(TodoAggregateTodoMarkedAsCompletedEventPayload.class, "TodoAggregateTodoMarkedAsCompletedEventPayload"));
        }

    }

    private static class DefaultEncryptedEventSecret implements EncryptedEventSecret {

        @Override
        public String aggregateRootId() {
            return null;
        }

        @Override
        public String aggregateRootType() {
            return null;
        }

        @Override
        public String secret() {
            return "AAlwSnNqyIRebwRqBfHufaCTXoRFRllg";
        }

    }

    @Test
    public void should_deserialize() {
        // Given
        final EventPayloadDeserializer eventPayloadDeserializer = new JacksonEventPayloadDeserializer(new DefaultJacksonEventPayloadSubtypes());

        // When
        final EventPayload deserialized = eventPayloadDeserializer.deserialize(Optional.of(new DefaultEncryptedEventSecret()),
                "{\"@type\":\"TodoAggregateTodoMarkedAsCompletedEventPayload\",\"todoId\":\"todoId\"}");

        // Then
        assertEquals(new TodoAggregateTodoMarkedAsCompletedEventPayload("todoId"), deserialized);
    }

}
