package com.damdamdeo.todo.domain.event;

import com.damdamdeo.eventsourcing.domain.Payload;
import com.damdamdeo.todo.domain.TodoAggregateRoot;

import java.util.Objects;

public class TodoMarkedAsCompletedEventPayload extends Payload<TodoAggregateRoot> {

    private final String todoId;

    public TodoMarkedAsCompletedEventPayload(final String todoId) {
        this.todoId = Objects.requireNonNull(todoId);
    }

    public String todoId() {
        return todoId;
    }

    @Override
    protected void apply(final TodoAggregateRoot aggregateRoot) {
        aggregateRoot.on(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TodoMarkedAsCompletedEventPayload)) return false;
        TodoMarkedAsCompletedEventPayload that = (TodoMarkedAsCompletedEventPayload) o;
        return Objects.equals(todoId, that.todoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoId);
    }
}
