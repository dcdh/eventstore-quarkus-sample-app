package com.damdamdeo.email_notifier.consumer.event;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventPayloadConsumer;

import java.util.Objects;

public final class TodoAggregateTodoMarkedAsCompletedEventPayloadConsumer extends AggregateRootEventPayloadConsumer {

    private final String todoId;

    public TodoAggregateTodoMarkedAsCompletedEventPayloadConsumer(final String todoId) {
        this.todoId = Objects.requireNonNull(todoId);
    }

    public String todoId() {
        return todoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoAggregateTodoMarkedAsCompletedEventPayloadConsumer that = (TodoAggregateTodoMarkedAsCompletedEventPayloadConsumer) o;
        return Objects.equals(todoId, that.todoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoId);
    }
}
