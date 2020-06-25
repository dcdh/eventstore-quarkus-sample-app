package com.damdamdeo.email_notifier.infrastructure;

import com.damdamdeo.email_notifier.consumer.DefaultEventMetadataConsumer;
import com.damdamdeo.email_notifier.consumer.TodoAggregateRootMaterializedStateConsumer;
import com.damdamdeo.email_notifier.domain.TodoStatus;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.JacksonAggregateRootEventMetadataConsumer;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.JacksonAggregateRootMaterializedStateConsumer;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.spi.JacksonAggregateRootEventMetadataConsumerMixInSubtypeDiscovery;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.spi.JacksonAggregateRootEventPayloadConsumerMixInSubtypeDiscovery;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.spi.JacksonAggregateRootMaterializedStateConsumerMixInSubtypeDiscovery;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.spi.JacksonMixInSubtype;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.damdamdeo.eventsourced.encryption.infra.serialization.JacksonStringEncryptionDeserializer;

import javax.enterprise.inject.Produces;
import java.util.Collections;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class JacksonProducers {

    public static abstract class JacksonDefaultEventMetadataConsumer extends JacksonAggregateRootEventMetadataConsumer {

        @JsonCreator
        public JacksonDefaultEventMetadataConsumer() {

        }

    }

    public static abstract class JacksonTodoAggregateRootMaterializedStateConsumer extends JacksonAggregateRootMaterializedStateConsumer {

        @JsonCreator
        public JacksonTodoAggregateRootMaterializedStateConsumer(@JsonProperty("aggregateRootId") final String aggregateRootId,
                                                                 @JsonProperty("aggregateRootType") final String aggregateRootType,
                                                                 @JsonProperty("version") final Long version,
                                                                 @JsonProperty("description") @JsonDeserialize(using = JacksonStringEncryptionDeserializer.class) final String description,
                                                                 @JsonProperty("todoStatus") final TodoStatus todoStatus) {
        }

    }

    @Produces
    public JacksonAggregateRootEventMetadataConsumerMixInSubtypeDiscovery jacksonAggregateRootEventMetadataConsumerMixInSubtypeDiscovery() {
        return () -> Collections.singletonList(
                new JacksonMixInSubtype<>(DefaultEventMetadataConsumer.class, JacksonDefaultEventMetadataConsumer.class, "DefaultEventMetadata"));
    }

    @Produces
    public JacksonAggregateRootEventPayloadConsumerMixInSubtypeDiscovery jacksonAggregateRootEventPayloadConsumerMixInSubtypeDiscovery() {
        return () -> emptyList();
    }

    @Produces
    public JacksonAggregateRootMaterializedStateConsumerMixInSubtypeDiscovery jacksonAggregateRootMaterializedStateConsumerMixInSubtypeDiscovery() {
        return () -> singletonList(
                new JacksonMixInSubtype<>(TodoAggregateRootMaterializedStateConsumer.class, JacksonTodoAggregateRootMaterializedStateConsumer.class, "TodoAggregateRoot"));
    }

}
