package com.damdamdeo.todo.infrastructure;

import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.JacksonAggregateRootEventMetadataConsumer;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.JacksonAggregateRootEventPayloadConsumer;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.JacksonAggregateRootMaterializedStateConsumer;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.spi.JacksonAggregateRootEventMetadataConsumerMixInSubtypeDiscovery;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.spi.JacksonAggregateRootEventPayloadConsumerMixInSubtypeDiscovery;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.spi.JacksonAggregateRootMaterializedStateConsumerMixInSubtypeDiscovery;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.spi.JacksonMixInSubtype;
import com.damdamdeo.eventsourced.encryption.infra.serialization.JacksonEncryptionDeserializer;
import com.damdamdeo.todo.consumer.DefaultEventMetadataConsumer;
import com.damdamdeo.todo.consumer.TodoAggregateRootMaterializedStateConsumer;
import com.damdamdeo.todo.consumer.event.TodoAggregateTodoCreatedEventPayloadConsumer;
import com.damdamdeo.todo.consumer.event.TodoAggregateTodoMarkedAsCompletedEventPayloadConsumer;
import com.damdamdeo.todo.domain.api.TodoStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import javax.enterprise.inject.Produces;
import java.util.Arrays;
import java.util.Collections;

import static java.util.Collections.singletonList;

public class JacksonProducers {

    public static abstract class JacksonDefaultEventMetadataConsumer extends JacksonAggregateRootEventMetadataConsumer {

        @JsonCreator
        public JacksonDefaultEventMetadataConsumer() {

        }

    }

    public static abstract class JacksonTodoAggregateTodoCreatedEventPayloadConsumer extends JacksonAggregateRootEventPayloadConsumer {

        @JsonCreator
        public JacksonTodoAggregateTodoCreatedEventPayloadConsumer(@JsonProperty("todoId") String todoId,
                                                                   @JsonProperty("description") @JsonDeserialize(using = JacksonEncryptionDeserializer.class) String description) {

        }

    }

    public static abstract class JacksonTodoAggregateTodoMarkedAsCompletedEventPayloadConsumer extends JacksonAggregateRootEventPayloadConsumer {

        @JsonCreator
        public JacksonTodoAggregateTodoMarkedAsCompletedEventPayloadConsumer(@JsonProperty("todoId") String todoId) {

        }

    }

    public static abstract class JacksonTodoAggregateRootMaterializedStateConsumer extends JacksonAggregateRootMaterializedStateConsumer {

        @JsonCreator
        public JacksonTodoAggregateRootMaterializedStateConsumer(@JsonProperty("aggregateRootId") final String aggregateRootId,
                                                                 @JsonProperty("aggregateRootType") final String aggregateRootType,
                                                                 @JsonProperty("version") final Long version,
                                                                 @JsonProperty("description")
                                                                 @JsonDeserialize(using = JacksonEncryptionDeserializer.class)
                                                                 final String description,
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
        return () -> Arrays.asList(
                new JacksonMixInSubtype<>(TodoAggregateTodoCreatedEventPayloadConsumer.class, JacksonTodoAggregateTodoCreatedEventPayloadConsumer.class, "TodoAggregateTodoCreatedEventPayload"),
                new JacksonMixInSubtype<>(TodoAggregateTodoMarkedAsCompletedEventPayloadConsumer.class, JacksonTodoAggregateTodoMarkedAsCompletedEventPayloadConsumer.class, "TodoAggregateTodoMarkedAsCompletedEventPayload")
        );
    }

    @Produces
    public JacksonAggregateRootMaterializedStateConsumerMixInSubtypeDiscovery jacksonAggregateRootMaterializedStateConsumerMixInSubtypeDiscovery() {
        return () -> singletonList(
                new JacksonMixInSubtype<>(TodoAggregateRootMaterializedStateConsumer.class, JacksonTodoAggregateRootMaterializedStateConsumer.class, "TodoAggregateRoot"));
    }

}
