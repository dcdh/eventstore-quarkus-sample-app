package com.damdamdeo.todo.domain;

import com.damdamdeo.eventsourcing.domain.AggregateRoot;
import com.damdamdeo.todo.api.Todo;
import com.damdamdeo.todo.api.TodoStatus;
import com.damdamdeo.todo.domain.event.TodoCreatedEventPayload;
import com.damdamdeo.todo.domain.event.TodoMarkedAsCompletedEventPayload;

import java.util.Objects;

public class TodoAggregateRoot extends AggregateRoot implements Todo {

    private String description;

    private TodoStatus todoStatus;

    public TodoAggregateRoot() {}

    public TodoAggregateRoot(final String aggregateRootId,
                             final String description,
                             final TodoStatus todoStatus,
                             final Long version) {
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.description = Objects.requireNonNull(description);
        this.todoStatus = Objects.requireNonNull(todoStatus);
        this.version = Objects.requireNonNull(version);
    }

    public void on(final TodoCreatedEventPayload todoCreatedEventPayload) {
        this.aggregateRootId = todoCreatedEventPayload.todoId();
        this.description = todoCreatedEventPayload.description();
        this.todoStatus = TodoStatus.IN_PROGRESS;
    }

    public void on(final TodoMarkedAsCompletedEventPayload todoMarkedAsCompletedEventPayload) {
        this.todoStatus = TodoStatus.COMPLETED;
    }


    @Override
    public String todoId() {
        return aggregateRootId;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public TodoStatus todoStatus() {
        return todoStatus;
    }

    @Override
    public String toString() {
        return "TodoAggregateRoot{" +
                "description='" + description + '\'' +
                ", todoStatus=" + todoStatus +
                ", aggregateRootId='" + aggregateRootId + '\'' +
                ", version=" + version +
                '}';
    }
}
