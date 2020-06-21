package com.damdamdeo.todo.aggregate.event;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventPayload;
import com.damdamdeo.todo.aggregate.TodoAggregateRoot;

import java.util.Objects;

public final class TodoAggregateTodoMarkedAsCompletedEventPayload extends AggregateRootEventPayload<TodoAggregateRoot> {

    private final String todoId;

    public TodoAggregateTodoMarkedAsCompletedEventPayload(final String todoId) {
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
    public String eventPayloadName() {
        return "TodoMarkedAsCompletedEventPayload";
    }

    @Override
    public AggregateRootId aggregateRootId() {
        return new AggregateRootId() {
            @Override
            public String aggregateRootId() {
                return todoId;
            }

            @Override
            public String aggregateRootType() {
                return "TodoAggregateRoot";
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoAggregateTodoMarkedAsCompletedEventPayload that = (TodoAggregateTodoMarkedAsCompletedEventPayload) o;
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
