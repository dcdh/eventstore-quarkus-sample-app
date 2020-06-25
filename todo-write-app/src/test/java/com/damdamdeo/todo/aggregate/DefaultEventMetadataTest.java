package com.damdamdeo.todo.aggregate;

import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventMetadata;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.JacksonAggregateRootEventMetadataDeSerializer;
import io.quarkus.test.junit.QuarkusTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@QuarkusTest
public class DefaultEventMetadataTest {

    @Inject
    JacksonAggregateRootEventMetadataDeSerializer jacksonAggregateRootEventMetadataDeSerializer;

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(DefaultEventMetadata.class).verify();
    }

    @Test
    public void should_serialize() {
        // Given
        final Secret secret = mock(Secret.class);

        // When
        final String serialized = jacksonAggregateRootEventMetadataDeSerializer.serialize(secret, new DefaultEventMetadata());

        // Then
        assertEquals("{\"@type\":\"DefaultEventMetadata\"}", serialized);
        verify(secret, times(0)).secret();
    }

    @Test
    public void should_deserialize() {
        // Given
        final Secret secret = mock(Secret.class);

        // When
        final AggregateRootEventMetadata aggregateRootEventMetadata = jacksonAggregateRootEventMetadataDeSerializer.deserialize(secret,
                "{\"@type\":\"DefaultEventMetadata\"}");

        // Then
        assertEquals(new DefaultEventMetadata(), aggregateRootEventMetadata);
        verify(secret, times(0)).secret();
    }

}
