package com.damdamdeo.todo.domain.event;

import com.damdamdeo.eventsourcing.domain.Payload;
import com.damdamdeo.todo.domain.TodoAggregateRoot;

import java.util.Objects;

public class TodoCreatedEventPayload extends Payload<TodoAggregateRoot> {

    private final String todoId;

    private final String description;

    public TodoCreatedEventPayload(final String todoId,
                                   final String description) {
        this.todoId = Objects.requireNonNull(todoId);
        this.description = Objects.requireNonNull(description);
    }

    @Override
    protected void apply(final TodoAggregateRoot aggregateRoot) {
        aggregateRoot.on(this);
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
        TodoCreatedEventPayload that = (TodoCreatedEventPayload) o;
        return Objects.equals(todoId, that.todoId) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoId, description);
    }
}
