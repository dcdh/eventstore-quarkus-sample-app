package com.damdamdeo.todo.domain;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;
import com.damdamdeo.todo.domain.api.Todo;
import com.damdamdeo.todo.domain.api.TodoStatus;
import com.damdamdeo.todo.domain.api.specification.IsTodoNotMarkedAsCompletedSpecification;
import com.damdamdeo.todo.domain.command.CreateNewTodoCommand;
import com.damdamdeo.todo.domain.command.MarkTodoAsCompletedCommand;
import com.damdamdeo.todo.domain.event.TodoCreatedEventPayload;
import com.damdamdeo.todo.domain.event.TodoMarkedAsCompletedEventPayload;

import java.util.Objects;

public class TodoAggregateRoot extends AggregateRoot implements Todo {

    private String description;

    private TodoStatus todoStatus;

    public TodoAggregateRoot(final String aggregateRootId) {
        super(aggregateRootId);
    }

    private TodoAggregateRoot(final Builder builder) {
        super(builder.aggregateRootId, builder.version);
        this.description = builder.description;
        this.todoStatus = builder.todoStatus;
    }

    public void handle(final CreateNewTodoCommand createNewTodoCommand, final String todoId) {
        this.apply("TodoCreatedEvent",
                new TodoCreatedEventPayload(todoId,
                createNewTodoCommand.description()));
    }

    public void handle(final MarkTodoAsCompletedCommand markTodoAsCompletedCommand,
                       final IsTodoNotMarkedAsCompletedSpecification specification) {
        specification.checkSatisfiedBy(this);
        this.apply("TodoMarkedAsCompletedEvent",
                new TodoMarkedAsCompletedEventPayload(markTodoAsCompletedCommand.todoId()));
    }

    public void on(final TodoCreatedEventPayload todoCreatedEventPayload) {
        this.description = todoCreatedEventPayload.description();
        this.todoStatus = TodoStatus.IN_PROGRESS;
    }

    public void on(final TodoMarkedAsCompletedEventPayload todoMarkedAsCompletedEventPayload) {
        this.todoStatus = TodoStatus.COMPLETED;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private String aggregateRootId;

        private Long version;

        private String description;

        private TodoStatus todoStatus;

        private Builder() {};

        public Builder withAggregateRootId(final String aggregateRootId) {
            this.aggregateRootId = aggregateRootId;
            return this;
        }

        public Builder withVersion(final Long version) {
            this.version = version;
            return this;
        }

        public Builder withDescription(final String description) {
            this.description = description;
            return this;
        }

        public Builder withTodoStatus(final TodoStatus todoStatus) {
            this.todoStatus = todoStatus;
            return this;
        }

        public TodoAggregateRoot build() {
            return new TodoAggregateRoot(this);
        }

    }

    @Override
    public String todoId() {
        return aggregateRootId().aggregateRootId();
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoAggregateRoot that = (TodoAggregateRoot) o;
        return Objects.equals(description, that.description) &&
                todoStatus == that.todoStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, todoStatus);
    }

    @Override
    public String toString() {
        return "TodoAggregateRoot{" +
                "description='" + description + '\'' +
                ", todoStatus=" + todoStatus +
                '}';
    }
}
