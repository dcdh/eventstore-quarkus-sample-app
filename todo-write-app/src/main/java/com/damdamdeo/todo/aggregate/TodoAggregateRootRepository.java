package com.damdamdeo.todo.aggregate;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.UnknownAggregateRootException;

public interface TodoAggregateRootRepository {

    boolean isTodoExistent(String todoIdToCheck);

    TodoAggregateRoot save(TodoAggregateRoot aggregateRoot);

    TodoAggregateRoot load(String aggregateRootId) throws UnknownAggregateRootException;

}
