package com.damdamdeo.todo.aggregate;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;
import com.damdamdeo.todo.aggregate.event.DefaultEventMetadata;
import com.damdamdeo.todo.api.Todo;
import com.damdamdeo.todo.api.TodoStatus;
import com.damdamdeo.todo.aggregate.event.TodoCreatedEventPayload;
import com.damdamdeo.todo.aggregate.event.TodoMarkedAsCompletedEventPayload;
import com.damdamdeo.todo.command.CreateNewTodoCommand;
import com.damdamdeo.todo.command.MarkTodoAsCompletedCommand;

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

    public void handle(final CreateNewTodoCommand createNewTodoCommand) {
        this.apply(new TodoCreatedEventPayload(createNewTodoCommand.todoId(),
                createNewTodoCommand.description()), new DefaultEventMetadata());
    }

    public void handle(final MarkTodoAsCompletedCommand markTodoAsCompletedCommand) {
        this.apply(new TodoMarkedAsCompletedEventPayload(markTodoAsCompletedCommand.todoId()), new DefaultEventMetadata());
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
