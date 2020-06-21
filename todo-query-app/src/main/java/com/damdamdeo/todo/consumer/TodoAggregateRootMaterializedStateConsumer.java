package com.damdamdeo.todo.consumer;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootMaterializedStateConsumer;
import com.damdamdeo.todo.domain.api.TodoStatus;

import java.util.Objects;

public final class TodoAggregateRootMaterializedStateConsumer extends AggregateRootMaterializedStateConsumer {

    private final String description;
    private final TodoStatus todoStatus;

    public TodoAggregateRootMaterializedStateConsumer(final String aggregateRootId,
                                                      final String aggregateRootType,
                                                      final Long version,
                                                      final String description,
                                                      final TodoStatus todoStatus) {
        super(aggregateRootId, aggregateRootType, version);
        this.description = Objects.requireNonNull(description);
        this.todoStatus = Objects.requireNonNull(todoStatus);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        TodoAggregateRootMaterializedStateConsumer that = (TodoAggregateRootMaterializedStateConsumer) o;
        return Objects.equals(description, that.description) &&
                todoStatus == that.todoStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), description, todoStatus);
    }

}
