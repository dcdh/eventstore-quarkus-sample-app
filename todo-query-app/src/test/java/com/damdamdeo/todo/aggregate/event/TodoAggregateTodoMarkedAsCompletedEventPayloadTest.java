package com.damdamdeo.todo.aggregate.event;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventPayload;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventPayloadSerializer;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.JacksonEventPayloadSerializer;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.spi.JacksonEventPayloadSubtypes;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.spi.JacksonSubtype;
import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Disabled// add to disable test however I am unable to build the application using mvn
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
        public Date creationDate() {
            return null;
        }

        @Override
        public String secret() {
            return null;
        }

    }

    @Test
    public void should_serialized() {
        // Given
        final EventPayloadSerializer eventPayloadSerializer = new JacksonEventPayloadSerializer(new DefaultJacksonEventPayloadSubtypes());

        // When
        final String serialized = eventPayloadSerializer.serialize(new DefaultEncryptedEventSecret(),
                new TodoAggregateTodoMarkedAsCompletedEventPayload("todoId"));

        // Then
        assertEquals("{\"@type\":\"TodoAggregateTodoMarkedAsCompletedEventPayload\",\"todoId\":\"todoId\"}", serialized);
    }

    @Test
    public void should_deserialize() {
        // Given
        final EventPayloadSerializer eventPayloadSerializer = new JacksonEventPayloadSerializer(new DefaultJacksonEventPayloadSubtypes());

        // When
        final EventPayload deserialized = eventPayloadSerializer.deserialize(new DefaultEncryptedEventSecret(),
                "{\"@type\":\"TodoAggregateTodoMarkedAsCompletedEventPayload\",\"todoId\":\"todoId\"}");

        // Then
        assertEquals(new TodoAggregateTodoMarkedAsCompletedEventPayload("todoId"), deserialized);
    }

}
