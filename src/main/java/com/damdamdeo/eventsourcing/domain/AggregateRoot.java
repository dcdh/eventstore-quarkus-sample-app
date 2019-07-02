package com.damdamdeo.eventsourcing.domain;

import java.io.Serializable;
import java.util.*;

public abstract class AggregateRoot implements Serializable {

    protected String aggregateRootId;
    private final transient List<Event> unsavedEvents = new LinkedList<>();
    private Long version = -1l;

    public void apply(final Payload payload) {
        this.apply(payload, new HashMap<>());
    }

    public void apply(final Payload payload, final Map<String, Object> metaData) {
        payload.apply(this);
        this.version++;
        this.unsavedEvents.add(new Event(UUID.randomUUID(),
                Objects.requireNonNull(aggregateRootId, "aggregateRootId must not be null please ensure it was set by the creational event !"),
                this.getClass().getSimpleName(),
                payload.getClass().getSimpleName().replaceFirst("(^.+)Payload$", "$1"),
                this.version,
                new Date(),
                payload,
                metaData));
    }

    public void loadFromHistory(final List<Event> events) {
        events.forEach(event -> {
            event.payload().apply(this);
            this.version = event.version();
        });
    }

    public List<Event> unsavedEvents() {
        return new ArrayList<Event>(unsavedEvents);
    }

    public void deleteUnsavedEvents() {
        unsavedEvents.clear();
    }

    public Long version() {
        return version;
    }

    public String aggregateRootId() {
        return aggregateRootId;
    }

}
