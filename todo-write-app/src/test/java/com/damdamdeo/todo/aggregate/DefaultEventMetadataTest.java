package com.damdamdeo.todo.aggregate;

import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventMetadata;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.JacksonAggregateRootEventMetadataDeSerializer;
import io.quarkus.test.junit.QuarkusTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Optional;

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
        final AggregateRootSecret aggregateRootSecret = mock(AggregateRootSecret.class);

        // When
        final String serialized = jacksonAggregateRootEventMetadataDeSerializer.serialize(Optional.of(aggregateRootSecret), new DefaultEventMetadata());

        // Then
        assertEquals("{\"@type\":\"DefaultEventMetadata\"}", serialized);
        verify(aggregateRootSecret, times(0)).secret();
    }

    @Test
    public void should_deserialize() {
        // Given
        final AggregateRootSecret aggregateRootSecret = mock(AggregateRootSecret.class);

        // When
        final AggregateRootEventMetadata aggregateRootEventMetadata = jacksonAggregateRootEventMetadataDeSerializer.deserialize(Optional.empty(),
                "{\"@type\":\"DefaultEventMetadata\"}");

        // Then
        assertEquals(new DefaultEventMetadata(), aggregateRootEventMetadata);
        verify(aggregateRootSecret, times(0)).secret();
    }

}
