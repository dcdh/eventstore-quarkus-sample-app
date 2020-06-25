package com.damdamdeo.email_notifier.consumer;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventMetadataConsumer;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.JacksonAggregateRootEventMetadataConsumerDeserializer;
import com.damdamdeo.eventsourced.encryption.api.Secret;
import io.quarkus.test.junit.QuarkusTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

@QuarkusTest
public class DefaultEventMetadataConsumerTest {

    @Inject
    JacksonAggregateRootEventMetadataConsumerDeserializer jacksonAggregateRootEventMetadataConsumerDeserializer;

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(DefaultEventMetadataConsumer.class).verify();
    }

    @Test
    public void should_deserialize() {
        // Given

        // When
        final AggregateRootEventMetadataConsumer deserialized = jacksonAggregateRootEventMetadataConsumerDeserializer.deserialize(mock(Secret.class),
                "{\"@type\":\"DefaultEventMetadata\"}");

        // Then
        assertEquals(new DefaultEventMetadataConsumer(), deserialized);
    }

}