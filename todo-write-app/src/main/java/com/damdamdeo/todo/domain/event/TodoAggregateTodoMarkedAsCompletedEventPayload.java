package com.damdamdeo.todo.domain.event;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayload;
import com.damdamdeo.todo.domain.TodoAggregateRoot;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TodoAggregateTodoMarkedAsCompletedEventPayload extends AggregateRootEventPayload<TodoAggregateRoot> {

    private final String todoId;

    @JsonCreator
    public TodoAggregateTodoMarkedAsCompletedEventPayload(@JsonProperty("todoId") final String todoId) {
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
    public String eventName() {
        return "TodoMarkedAsCompletedEvent";
    }

    @Override
    public String aggregateRootId() {
        return todoId;
    }

    @Override
    public String aggregateRootType() {
        return "TodoAggregateRoot";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TodoAggregateTodoMarkedAsCompletedEventPayload)) return false;
        TodoAggregateTodoMarkedAsCompletedEventPayload that = (TodoAggregateTodoMarkedAsCompletedEventPayload) o;
        return Objects.equals(todoId, that.todoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoId);
    }

    @Override
    public String toString() {
        return "TodoMarkedAsCompletedEventPayload{" +
                "todoId='" + todoId + '\'' +
                '}';
    }
}
