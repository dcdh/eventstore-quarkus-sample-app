package com.damdamdeo.todo.aggregate.event;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventPayload;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.Optional;
import java.util.stream.Stream;

public enum EventPayloadTypeEnum {

    TODO_CREATED_TODO_PAYLOAD("TodoAggregateRoot", "TodoCreatedEventPayload") {

        @Override
        public JsonObject toJsonObject(final EventPayload eventPayload) {
            return Json.createObjectBuilder()
                    .add(PAYLOAD_TYPE, eventPayloadType())
                    .add(AGGREGATE_ROOT_TYPE, aggregateRootType())
                    .add("todoId", ((TodoCreatedEventPayload) eventPayload).todoId())
                    .add("description", ((TodoCreatedEventPayload) eventPayload).description())
                    .build();
        }

        @Override
        public EventPayload toEventPayload(final JsonObject eventPayload) {
            return new TodoCreatedEventPayload(eventPayload.getString("todoId"),
                    eventPayload.getString("description"));
        }

    },

    TODO_MARK_AS_COMPLETED_TODO_PAYLOAD("TodoAggregateRoot", "TodoMarkedAsCompletedEventPayload") {

        @Override
        public JsonObject toJsonObject(final EventPayload eventPayload) {
            return Json.createObjectBuilder()
                    .add(PAYLOAD_TYPE, eventPayloadType())
                    .add(AGGREGATE_ROOT_TYPE, aggregateRootType())
                    .add("todoId", ((TodoMarkedAsCompletedEventPayload) eventPayload).todoId())
                    .build();
        }

        @Override
        public EventPayload toEventPayload(final JsonObject eventPayload) {
            return new TodoMarkedAsCompletedEventPayload(eventPayload.getString("todoId"));
        }

    };

    public final static String AGGREGATE_ROOT_TYPE = "@aggregaterootType";
    public final static String PAYLOAD_TYPE = "@payloadType";

    private final String aggregateRootType;
    private final String eventPayloadType;

    EventPayloadTypeEnum(final String aggregateRootType,
                         final String eventPayloadType) {
        this.aggregateRootType = aggregateRootType;
        this.eventPayloadType = eventPayloadType;
    }

    public abstract JsonObject toJsonObject(EventPayload eventPayload);

    public abstract EventPayload toEventPayload(JsonObject eventPayload);

    public String aggregateRootType() {
        return aggregateRootType;
    }

    public String eventPayloadType() {
        return eventPayloadType;
    }

    public static Optional<EventPayloadTypeEnum> from(final String aggregateRootType,
                                                      final String eventPayloadType) {
        return Stream.of(EventPayloadTypeEnum.values())
                .filter(eventPayloadTypeEnum -> eventPayloadTypeEnum.aggregateRootType.equals(aggregateRootType)
                        && eventPayloadTypeEnum.eventPayloadType.equals(eventPayloadType))
                .findFirst();
    }

}
