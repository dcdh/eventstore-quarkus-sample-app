package com.damdamdeo.eventsourcing.domain;

public interface AggregateRootRepository<T extends AggregateRoot> {

    T save(T aggregateRoot);

    T load(String aggregateRootId) throws UnknownAggregateRootException;

}
