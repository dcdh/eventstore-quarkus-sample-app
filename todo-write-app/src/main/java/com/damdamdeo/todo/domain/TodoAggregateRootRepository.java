package com.damdamdeo.todo.domain;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootRepository;

public interface TodoAggregateRootRepository extends AggregateRootRepository<TodoAggregateRoot> {

    boolean isTodoExistent(String todoIdToCheck);

}
