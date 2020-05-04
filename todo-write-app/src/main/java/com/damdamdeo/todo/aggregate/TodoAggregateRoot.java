package com.damdamdeo.todo.aggregate;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;
import com.damdamdeo.todo.aggregate.event.TodoAggregateTodoCreatedAggregateRootEventPayload;
import com.damdamdeo.todo.aggregate.event.TodoAggregateTodoMarkedAsCompletedAggregateRootEventPayload;
import com.damdamdeo.todo.command.CreateNewTodoCommand;
import com.damdamdeo.todo.command.MarkTodoAsCompletedCommand;
import com.damdamdeo.todo.domain.api.Todo;
import com.damdamdeo.todo.domain.api.TodoStatus;
import com.damdamdeo.todo.domain.api.event.DefaultEventMetadata;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TodoAggregateRoot extends AggregateRoot implements Todo {

    private String description;

    private TodoStatus todoStatus;

    public TodoAggregateRoot() {}

    @JsonCreator
    public TodoAggregateRoot(@JsonProperty("aggregateRootId") final String aggregateRootId,
                             @JsonProperty("description") final String description,
                             @JsonProperty("todoStatus") final TodoStatus todoStatus,
                             @JsonProperty("version") final Long version) {
        this.aggregateRootId = Objects.requireNonNull(aggregateRootId);
        this.description = Objects.requireNonNull(description);
        this.todoStatus = Objects.requireNonNull(todoStatus);
        this.version = Objects.requireNonNull(version);
    }

    public void handle(final CreateNewTodoCommand createNewTodoCommand, final String todoId) {
        this.apply(new TodoAggregateTodoCreatedAggregateRootEventPayload(todoId,
                createNewTodoCommand.description()), new DefaultEventMetadata());
    }

    public void handle(final MarkTodoAsCompletedCommand markTodoAsCompletedCommand) {
        this.apply(new TodoAggregateTodoMarkedAsCompletedAggregateRootEventPayload(markTodoAsCompletedCommand.todoId()), new DefaultEventMetadata());
    }

    public void on(final TodoAggregateTodoCreatedAggregateRootEventPayload todoAggregateTodoCreatedAggregateRootEventPayload) {
        this.aggregateRootId = todoAggregateTodoCreatedAggregateRootEventPayload.todoId();
        this.description = todoAggregateTodoCreatedAggregateRootEventPayload.description();
        this.todoStatus = TodoStatus.IN_PROGRESS;
    }

    public void on(final TodoAggregateTodoMarkedAsCompletedAggregateRootEventPayload todoAggregateTodoMarkedAsCompletedAggregateRootEventPayload) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TodoAggregateRoot)) return false;
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
                ", aggregateRootId='" + aggregateRootId + '\'' +
                ", version=" + version +
                '}';
    }
}
