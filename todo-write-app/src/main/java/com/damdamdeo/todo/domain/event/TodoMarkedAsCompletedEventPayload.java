package com.damdamdeo.todo.domain.event;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRootEventPayload;
import com.damdamdeo.todo.domain.TodoAggregateRoot;

import java.util.Objects;

public final class TodoMarkedAsCompletedEventPayload implements AggregateRootEventPayload<TodoAggregateRoot> {

    private final String todoId;

    public TodoMarkedAsCompletedEventPayload(final String todoId) {
        this.todoId = Objects.requireNonNull(todoId);
    }

    public String todoId() {
        return todoId;
    }

    @Override
    public void apply(final TodoAggregateRoot aggregateRoot) {
        aggregateRoot.on(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoMarkedAsCompletedEventPayload that = (TodoMarkedAsCompletedEventPayload) o;
        return Objects.equals(todoId, that.todoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoId);
    }

    @Override
    public String toString() {
        return "TodoAggregateTodoMarkedAsCompletedEventPayload{" +
                "todoId='" + todoId + '\'' +
                '}';
    }
}
