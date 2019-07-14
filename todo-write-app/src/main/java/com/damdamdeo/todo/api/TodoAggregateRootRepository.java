package com.damdamdeo.todo.api;

import com.damdamdeo.eventsourcing.domain.AggregateRootRepository;
import com.damdamdeo.todo.domain.TodoAggregateRoot;

public interface TodoAggregateRootRepository extends AggregateRootRepository<TodoAggregateRoot> {

    boolean isTodoIdAffected(String todoIdToCheck);

}
