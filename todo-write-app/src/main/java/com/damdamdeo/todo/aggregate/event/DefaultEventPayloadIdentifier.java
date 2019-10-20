package com.damdamdeo.todo.aggregate.event;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventPayloadIdentifier;

public final class DefaultEventPayloadIdentifier implements EventPayloadIdentifier {

    private final String aggregateRootId;
    private final EventPayloadTypeEnum eventPayloadTypeEnun;

    public DefaultEventPayloadIdentifier(final String aggregateRootId,
                                         final EventPayloadTypeEnum eventPayloadTypeEnun) {
        this.aggregateRootId = aggregateRootId;
        this.eventPayloadTypeEnun = eventPayloadTypeEnun;
    }

    @Override
    public String aggregateRootType() {
        return eventPayloadTypeEnun.aggregateRootType();
    }

    @Override
    public String eventPayloadType() {
        return eventPayloadTypeEnun.eventPayloadType();
    }

    @Override
    public String aggregateRootId() {
        return aggregateRootId;
    }

}
