package com.damdamdeo.todo.consumer.event;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventPayloadConsumer;

import java.util.Objects;

public final class TodoAggregateTodoCreatedEventPayloadConsumer extends AggregateRootEventPayloadConsumer {

    private final String todoId;
    private final String description;

    public TodoAggregateTodoCreatedEventPayloadConsumer(final String todoId, final String description) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoAggregateTodoCreatedEventPayloadConsumer that = (TodoAggregateTodoCreatedEventPayloadConsumer) o;
        return Objects.equals(todoId, that.todoId) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoId, description);
    }
}
