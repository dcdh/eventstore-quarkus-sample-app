package com.damdamdeo.eventsourcing.domain;

public interface AggregateRootProjectionRepository {

    AggregateRootProjection save(AggregateRootProjection aggregateRootProjection);

}
