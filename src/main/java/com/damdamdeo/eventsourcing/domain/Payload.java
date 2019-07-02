package com.damdamdeo.eventsourcing.domain;

public abstract class Payload<T extends AggregateRoot> {

    protected abstract void apply(T aggregateRoot);

}
