package com.damdamdeo.todo.infrastructure;

import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.spi.JacksonAggregateRootEventMetadataConsumerMixInSubtypeDiscovery;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.spi.JacksonAggregateRootEventPayloadConsumerMixInSubtypeDiscovery;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.spi.JacksonAggregateRootMaterializedStateConsumerMixInSubtypeDiscovery;

import javax.enterprise.inject.Produces;
import java.util.Collections;

public class JacksonConsumerProducers {

    @Produces
    public JacksonAggregateRootEventMetadataConsumerMixInSubtypeDiscovery jacksonAggregateRootEventMetadataConsumerMixInSubtypeDiscovery() {
        return () -> Collections.emptyList();
    }

    @Produces
    public JacksonAggregateRootEventPayloadConsumerMixInSubtypeDiscovery jacksonAggregateRootEventPayloadConsumerMixInSubtypeDiscovery() {
        return () -> Collections.emptyList();
    }

    @Produces
    public JacksonAggregateRootMaterializedStateConsumerMixInSubtypeDiscovery jacksonAggregateRootMaterializedStateConsumerMixInSubtypeDiscovery() {
        return () -> Collections.emptyList();
    }

}
