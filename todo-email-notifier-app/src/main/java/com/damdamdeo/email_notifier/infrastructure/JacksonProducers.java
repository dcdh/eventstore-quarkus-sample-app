package com.damdamdeo.email_notifier.infrastructure;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventMetadata;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventPayload;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.spi.JacksonEventMetadataSubtypes;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.spi.JacksonEventPayloadSubtypes;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.spi.JacksonSubtype;
import com.damdamdeo.todo.domain.api.event.DefaultEventMetadata;
import com.damdamdeo.todo.domain.api.event.TodoAggregateTodoCreatedEventPayload;
import com.damdamdeo.todo.domain.api.event.TodoAggregateTodoMarkedAsCompletedEventPayload;

import javax.enterprise.inject.Produces;
import java.util.Arrays;
import java.util.List;

public class JacksonProducers {

    @Produces
    public JacksonEventMetadataSubtypes jacksonEventMetadataSubtypes() {
        return new JacksonEventMetadataSubtypes() {

            @Override
            public List<JacksonSubtype<EventMetadata>> jacksonSubtypes() {
                return Arrays.asList(new JacksonSubtype<>(DefaultEventMetadata.class, "DefaultEventMetadata"));
            }

        };
    }

    @Produces
    public JacksonEventPayloadSubtypes jacksonEventPayloadSubtypes() {
        return new JacksonEventPayloadSubtypes() {

            @Override
            public List<JacksonSubtype<EventPayload>> jacksonSubtypes() {
                return Arrays.asList(new JacksonSubtype<>(TodoAggregateTodoCreatedEventPayload.class, "TodoAggregateTodoCreatedEventPayload"),
                        new JacksonSubtype<>(TodoAggregateTodoMarkedAsCompletedEventPayload.class, "TodoAggregateTodoMarkedAsCompletedEventPayload"));
            }

        };
    }

}
