package com.damdamdeo.todo.aggregate.event;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRootEventPayload;
import com.damdamdeo.todo.aggregate.TodoAggregateRoot;

import java.util.Objects;

public final class TodoCreatedEventPayload implements AggregateRootEventPayload<TodoAggregateRoot> {

    private final String todoId;
    private final String description;

    public TodoCreatedEventPayload(final String todoId, final String description) {
        this.todoId = Objects.requireNonNull(todoId);
        this.description = Objects.requireNonNull(description);
    }

    public String todoId() {
        return todoId;
    }

    public String description() {
        return description;
    }

    @Override
    public void apply(TodoAggregateRoot aggregateRoot) {
        aggregateRoot.on(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoCreatedEventPayload that = (TodoCreatedEventPayload) o;
        return Objects.equals(todoId, that.todoId) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoId, description);
    }

    @Override
    public String toString() {
        return "TodoAggregateTodoCreatedEventPayload{" +
                "todoId='" + todoId + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
