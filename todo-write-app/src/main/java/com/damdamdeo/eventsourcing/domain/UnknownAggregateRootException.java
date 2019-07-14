package com.damdamdeo.eventsourcing.domain;

import java.util.Objects;

public class UnknownAggregateRootException extends RuntimeException {

    private final String unknownAggregateId;

    public UnknownAggregateRootException(final String unknownAggregateId) {
        this.unknownAggregateId = Objects.requireNonNull(unknownAggregateId);
    }

    public String unknownAggregateId() {
        return unknownAggregateId;
    }

}
