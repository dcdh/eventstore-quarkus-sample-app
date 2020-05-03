package com.damdamdeo.todo.aggregate;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootRepository;

public interface TodoAggregateRootRepository extends AggregateRootRepository<TodoAggregateRoot> {

    boolean isTodoExistent(String todoIdToCheck);

}
