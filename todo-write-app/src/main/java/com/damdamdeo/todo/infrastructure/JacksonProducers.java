package com.damdamdeo.todo.infrastructure;

import com.damdamdeo.eventsourced.encryption.infra.serialization.JacksonStringEncryptionDeserializer;
import com.damdamdeo.eventsourced.encryption.infra.serialization.JacksonStringEncryptionSerializer;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.JacksonAggregateRootEventMetadata;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.JacksonAggregateRootEventPayload;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.JacksonAggregateRootMaterializedState;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.spi.JacksonAggregateRootEventMetadataMixInSubtypeDiscovery;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.spi.JacksonAggregateRootEventPayloadMixInSubtypeDiscovery;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.spi.JacksonAggregateRootMaterializedStateMixInSubtypeDiscovery;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.spi.JacksonMixInSubtype;
import com.damdamdeo.todo.aggregate.DefaultEventMetadata;
import com.damdamdeo.todo.aggregate.TodoAggregateRoot;
import com.damdamdeo.todo.aggregate.event.*;
import com.damdamdeo.todo.domain.api.TodoStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.enterprise.inject.Produces;
import java.util.Arrays;
import java.util.Collections;

import static java.util.Collections.singletonList;

public class JacksonProducers {

    public static abstract class JacksonDefaultEventMetadata extends JacksonAggregateRootEventMetadata {

        @JsonCreator
        public JacksonDefaultEventMetadata() {

        }

    }

    public static abstract class JacksonTodoAggregateTodoCreatedEventPayload extends JacksonAggregateRootEventPayload {

        @JsonCreator
        public JacksonTodoAggregateTodoCreatedEventPayload(@JsonProperty("todoId") final String todoId,
                                                           @JsonProperty("description")
                                                           @JsonSerialize(using = JacksonStringEncryptionSerializer.class)
                                                           @JsonDeserialize(using = JacksonStringEncryptionDeserializer.class) final String description) {

        }

    }

    public static abstract class JacksonTodoAggregateTodoMarkedAsCompletedEventPayload extends JacksonAggregateRootEventPayload {

        @JsonCreator
        public JacksonTodoAggregateTodoMarkedAsCompletedEventPayload(@JsonProperty("todoId") final String todoId) {

        }

    }

    public static abstract class JacksonTodoAggregateRootMaterializedState extends JacksonAggregateRootMaterializedState {

        @JsonCreator
        public JacksonTodoAggregateRootMaterializedState(@JsonProperty("aggregateRootId") final String aggregateRootId,
                                                         @JsonProperty("aggregateRootType") final String aggregateRootType,
                                                         @JsonProperty("version") final Long version,
                                                         @JsonProperty("description")
                                                         @JsonSerialize(using = JacksonStringEncryptionSerializer.class)
                                                         @JsonDeserialize(using = JacksonStringEncryptionDeserializer.class) final String description,
                                                         @JsonProperty("todoStatus") final TodoStatus todoStatus) {
        }

    }

    @Produces
    public JacksonAggregateRootEventMetadataMixInSubtypeDiscovery jacksonAggregateRootEventMetadataMixInSubtypeDiscovery() {
        return () -> Collections.singletonList(
                new JacksonMixInSubtype<>(DefaultEventMetadata.class, JacksonDefaultEventMetadata.class, "DefaultEventMetadata"));
    }

    @Produces
    public JacksonAggregateRootEventPayloadMixInSubtypeDiscovery jacksonAggregateRootEventPayloadMixInSubtypeDiscovery() {
        return () -> Arrays.asList(
                new JacksonMixInSubtype<>(TodoAggregateTodoCreatedEventPayload.class, JacksonTodoAggregateTodoCreatedEventPayload.class, "TodoAggregateTodoCreatedEventPayload"),
                new JacksonMixInSubtype<>(TodoAggregateTodoMarkedAsCompletedEventPayload.class, JacksonTodoAggregateTodoMarkedAsCompletedEventPayload.class, "TodoAggregateTodoMarkedAsCompletedEventPayload")
        );
    }

    @Produces
    public JacksonAggregateRootMaterializedStateMixInSubtypeDiscovery jacksonAggregateRootMaterializedStateMixInSubtypeDiscovery() {
        return () -> singletonList(
                new JacksonMixInSubtype<>(TodoAggregateRoot.class, JacksonTodoAggregateRootMaterializedState.class, "TodoAggregateRoot"));
    }
}
