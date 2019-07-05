package com.damdamdeo.eventsourcing.domain;

import java.util.Objects;

public class AggregateRootProjection {

    private final String aggregateRootId;

    private final String aggregateRootType;

    private final AggregateRoot aggregateRoot;

    private final Long version;

    public AggregateRootProjection(final AggregateRoot aggregateRoot) {
        this.aggregateRoot = Objects.requireNonNull(aggregateRoot);
        this.aggregateRootId = aggregateRoot.aggregateRootId();
        this.aggregateRootType = aggregateRoot.getClass().getSimpleName();
        this.version = aggregateRoot.version();
    }

    public String aggregateRootId() {
        return aggregateRootId;
    }

    public String aggregateRootType() {
        return aggregateRootType;
    }

    public AggregateRoot aggregateRoot() {
        return aggregateRoot;
    }

    public Long version() {
        return version;
    }
}
