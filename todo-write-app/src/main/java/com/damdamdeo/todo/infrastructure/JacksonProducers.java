package com.damdamdeo.todo.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventMetadata;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventPayload;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.spi.JacksonEventMetadataSubtypes;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.spi.JacksonEventPayloadSubtypes;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.spi.JacksonSubtype;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayload;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.spi.JacksonAggregateRootEventPayloadSubtypes;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.spi.JacksonAggregateRootSubtypes;
import com.damdamdeo.todo.aggregate.TodoAggregateRoot;
import com.damdamdeo.todo.aggregate.event.TodoAggregateTodoCreatedAggregateRootEventPayload;
import com.damdamdeo.todo.aggregate.event.TodoAggregateTodoMarkedAsCompletedAggregateRootEventPayload;
import com.damdamdeo.todo.domain.api.event.DefaultEventMetadata;

import javax.enterprise.inject.Produces;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

public class JacksonProducers {

    @Produces
    public JacksonAggregateRootEventPayloadSubtypes jacksonAggregateRootEventPayloadSubtypes() {
        return new JacksonAggregateRootEventPayloadSubtypes() {

            @Override
            public List<JacksonSubtype<AggregateRootEventPayload>> jacksonSubtypes() {
                return asList(new JacksonSubtype<>(TodoAggregateTodoCreatedAggregateRootEventPayload.class, "TodoAggregateTodoCreatedEventPayload"),
                        new JacksonSubtype<>(TodoAggregateTodoMarkedAsCompletedAggregateRootEventPayload.class, "TodoAggregateTodoMarkedAsCompletedEventPayload"));
            }

        };
    }

    @Produces
    public JacksonEventMetadataSubtypes jacksonEventMetadataSubtypes() {
        return new JacksonEventMetadataSubtypes() {

            @Override
            public List<JacksonSubtype<EventMetadata>> jacksonSubtypes() {
                return asList(new JacksonSubtype<>(DefaultEventMetadata.class, "DefaultEventMetadata"));
            }

        };
    }

    @Produces
    public JacksonEventPayloadSubtypes jacksonEventPayloadSubtypes() {
        return new JacksonEventPayloadSubtypes() {

            @Override
            public List<JacksonSubtype<EventPayload>> jacksonSubtypes() {
                return Collections.emptyList();
            }

        };
    }

    @Produces
    public JacksonAggregateRootSubtypes jacksonAggregateRootSubtypes() {
        return new JacksonAggregateRootSubtypes() {

            @Override
            public List<JacksonSubtype<AggregateRoot>> jacksonSubtypes() {
                return asList(new JacksonSubtype<>(TodoAggregateRoot.class, "TodoAggregateRoot"));
            }

        };
    }

}
