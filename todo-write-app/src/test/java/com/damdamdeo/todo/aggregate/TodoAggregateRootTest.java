package com.damdamdeo.todo.aggregate;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRootEvent;
import com.damdamdeo.todo.aggregate.event.TodoAggregateTodoCreatedEventPayload;
import com.damdamdeo.todo.aggregate.event.TodoAggregateTodoMarkedAsCompletedEventPayload;
import com.damdamdeo.todo.command.CreateNewTodoCommand;
import com.damdamdeo.todo.command.MarkTodoAsCompletedCommand;
import com.damdamdeo.todo.domain.api.TodoStatus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TodoAggregateRootTest {

    @Test
    public void should_create_todo() {
        // Given
        final TodoAggregateRoot todoAggregateRoot = new TodoAggregateRoot();

        // When
        todoAggregateRoot.handle(new CreateNewTodoCommand("lorem ipsum"), "todoId");

        // Then
        assertEquals("todoId", todoAggregateRoot.aggregateRootId().aggregateRootId());
        assertEquals("TodoAggregateRoot", todoAggregateRoot.aggregateRootId().aggregateRootType());
        assertEquals("lorem ipsum", todoAggregateRoot.description());
        assertEquals(TodoStatus.IN_PROGRESS, todoAggregateRoot.todoStatus());
        assertEquals(0l, todoAggregateRoot.version());

        final List<AggregateRootEvent> unsavedEvents = todoAggregateRoot.unsavedEvents();
        assertEquals(1, unsavedEvents.size());
        assertEquals(new TodoAggregateTodoCreatedEventPayload("todoId", "lorem ipsum"),
                unsavedEvents.get(0).eventPayload());
    }

    @Test
    public void should_mark_todo_as_completed() {
        // Given
        final TodoAggregateRoot todoAggregateRoot = new TodoAggregateRoot();
        todoAggregateRoot.handle(new CreateNewTodoCommand("lorem ipsum"), "todoId");

        // When
        todoAggregateRoot.handle(new MarkTodoAsCompletedCommand("todoId"));

        // Then
        assertEquals(TodoStatus.COMPLETED, todoAggregateRoot.todoStatus());
        assertEquals(1l, todoAggregateRoot.version());

        final List<AggregateRootEvent> unsavedEvents = todoAggregateRoot.unsavedEvents();
        assertEquals(2, unsavedEvents.size());
        assertEquals(new TodoAggregateTodoMarkedAsCompletedEventPayload("todoId"), unsavedEvents.get(1).eventPayload());
    }

}