package com.damdamdeo.eventsourcing.domain;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class Event {

    private final UUID eventId;
    private final String aggregateRootId;
    private final String aggregateRootType;
    private final String eventType;
    private final Long version;
    private final Date creationDate;
    private final Map<String, Object> metaData;
    private final Payload payload;

    public Event(final UUID eventId,
                 final String aggregateRootId,
                 final String aggregateRootType,
                 final String eventType,
                 final Long version,
                 final Date creationDate,
                 final Payload payload,
                 final Map<String, Object> metaData) {
        this.eventId = Objects.requireNonNull(eventId);
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.aggregateRootType = Objects.requireNonNull(aggregateRootType);
        this.eventType = Objects.requireNonNull(eventType);
        this.version = Objects.requireNonNull(version);
        this.creationDate = Objects.requireNonNull(creationDate);
        this.payload = Objects.requireNonNull(payload);
        this.metaData = Objects.requireNonNull(metaData);
    }

    public UUID eventId() {
        return eventId;
    }

    public String aggregateRootId() {
        return aggregateRootId;
    }

    public String aggregateRootType() {
        return aggregateRootType;
    }

    public String eventType() {
        return eventType;
    }

    public Long version() {
        return version;
    }

    public Date creationDate() {
        return creationDate;
    }

    public Payload payload() {
        return payload;
    }

    public Map<String, Object> metaData() {
        return metaData;
    }

}
