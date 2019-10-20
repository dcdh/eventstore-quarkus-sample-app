package com.damdamdeo.todo.aggregate.event;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventPayload;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventPayloadIdentifier;
import com.damdamdeo.todo.aggregate.TodoAggregateRoot;

import java.util.Objects;

public class TodoMarkedAsCompletedEventPayload extends EventPayload<TodoAggregateRoot> {

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
    public EventPayloadIdentifier eventPayloadIdentifier() {
        return new DefaultEventPayloadIdentifier(todoId,
                EventPayloadTypeEnum.TODO_MARK_AS_COMPLETED_TODO_PAYLOAD);
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
