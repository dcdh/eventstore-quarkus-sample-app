package com.damdamdeo.todo.user.type;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventPayload;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventPayloadIdentifier;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.hibernate.user.types.EventPayloadsAdapter;
import com.damdamdeo.todo.aggregate.event.EventPayloadTypeEnum;

import javax.json.JsonObject;
import javax.json.bind.adapter.JsonbAdapter;

public class DefaultEventPayloadsAdapter implements EventPayloadsAdapter,
        JsonbAdapter<EventPayload, JsonObject> // Quick fix an issue with yasson
    {

    @Override
    public JsonObject adaptToJson(final EventPayload eventPayload) {
        final EventPayloadIdentifier eventPayloadIdentifier = eventPayload.eventPayloadIdentifier();
        return EventPayloadTypeEnum.from(eventPayloadIdentifier.aggregateRootType(), eventPayloadIdentifier.eventPayloadType())
                .map(eventPayloadTypeEnum -> eventPayloadTypeEnum.toJsonObject(eventPayload))
                .orElseThrow(() -> new IllegalStateException(String.format("Unknown event payload type %s for event aggregate type %s for aggregaterootId %s",
                        eventPayloadIdentifier.eventPayloadType(),
                        eventPayloadIdentifier.aggregateRootType(),
                        eventPayloadIdentifier.aggregateRootId())));
    }

    @Override
    public EventPayload adaptFromJson(final JsonObject eventPayload) {
        final String aggregateRootType = eventPayload.getString(EventPayloadTypeEnum.AGGREGATE_ROOT_TYPE);
        final String eventPayloadType = eventPayload.getString(EventPayloadTypeEnum.PAYLOAD_TYPE);
        return EventPayloadTypeEnum.from(aggregateRootType, eventPayloadType)
                .map(eventPayloadTypeEnum -> eventPayloadTypeEnum.toEventPayload(eventPayload))
                .orElseThrow(() -> new IllegalStateException(String.format("Unknown event payload type %s for event aggregate type %s",
                        eventPayloadType,
                        aggregateRootType)));
    }

}
