package com.damdamdeo.todo.aggregate;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;
import com.damdamdeo.todo.aggregate.event.*;
import com.damdamdeo.todo.command.CreateNewTodoCommand;
import com.damdamdeo.todo.command.MarkTodoAsCompletedCommand;
import com.damdamdeo.todo.domain.api.Todo;
import com.damdamdeo.todo.domain.api.TodoStatus;

import java.util.Objects;

public class TodoAggregateRoot extends AggregateRoot implements Todo {

    private String description;

    private TodoStatus todoStatus;

    public TodoAggregateRoot() {}

    // needed to be able to well serialized using custom serializer de-serializer.
    public TodoAggregateRoot(final String aggregateRootId,
                             final String aggregateRootType,
                             final Long version,
                             final String description,
                             final TodoStatus todoStatus) {
        super(aggregateRootId, aggregateRootType, version);
        this.description = description;
        this.todoStatus = todoStatus;
    }

    public void handle(final CreateNewTodoCommand createNewTodoCommand, final String todoId) {
        this.apply("TodoCreatedEvent",
                new TodoAggregateTodoCreatedEventPayload(todoId,
                createNewTodoCommand.description()), new DefaultEventMetadata());
    }

    public void handle(final MarkTodoAsCompletedCommand markTodoAsCompletedCommand) {
        this.apply("TodoMarkedAsCompletedEvent",
                new TodoAggregateTodoMarkedAsCompletedEventPayload(markTodoAsCompletedCommand.todoId()),
                new DefaultEventMetadata());
    }

    public void on(final TodoAggregateTodoCreatedEventPayload todoAggregateTodoCreatedEventPayload) {
        this.description = todoAggregateTodoCreatedEventPayload.description();
        this.todoStatus = TodoStatus.IN_PROGRESS;
    }

    public void on(final TodoAggregateTodoMarkedAsCompletedEventPayload todoAggregateTodoMarkedAsCompletedEventPayload) {
        this.todoStatus = TodoStatus.COMPLETED;
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
