package com.damdamdeo.todo.domain;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRootEvent;
import com.damdamdeo.todo.domain.api.TodoStatus;
import com.damdamdeo.todo.domain.api.specification.IsTodoNotMarkedAsCompletedSpecification;
import com.damdamdeo.todo.domain.command.CreateNewTodoCommand;
import com.damdamdeo.todo.domain.command.MarkTodoAsCompletedCommand;
import com.damdamdeo.todo.domain.event.TodoCreatedEventPayload;
import com.damdamdeo.todo.domain.event.TodoMarkedAsCompletedEventPayload;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TodoAggregateRootTest {

    @Test
    public void should_create_todo() {
        // Given
        final TodoAggregateRoot todoAggregateRoot = new TodoAggregateRoot("todoId");

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
        assertEquals(new TodoCreatedEventPayload("todoId", "lorem ipsum"),
                unsavedEvents.get(0).eventPayload());
    }

    @Test
    public void should_mark_todo_as_completed() {
        // Given
        final TodoAggregateRoot todoAggregateRoot = new TodoAggregateRoot("todoId");
        todoAggregateRoot.handle(new CreateNewTodoCommand("lorem ipsum"), "todoId");
        final IsTodoNotMarkedAsCompletedSpecification specification = mock(IsTodoNotMarkedAsCompletedSpecification.class);

        // When
        todoAggregateRoot.handle(new MarkTodoAsCompletedCommand("todoId"), specification);

        // Then
        assertEquals(TodoStatus.COMPLETED, todoAggregateRoot.todoStatus());
        assertEquals(1l, todoAggregateRoot.version());

        final List<AggregateRootEvent> unsavedEvents = todoAggregateRoot.unsavedEvents();
        assertEquals(2, unsavedEvents.size());
        assertEquals(new TodoMarkedAsCompletedEventPayload("todoId"), unsavedEvents.get(1).eventPayload());
    }

    @Test
    public void should_check_todo_before_marking_it() {
        // Given
        final TodoAggregateRoot todoAggregateRoot = new TodoAggregateRoot("todoId");
        todoAggregateRoot.handle(new CreateNewTodoCommand("lorem ipsum"), "todoId");
        final IsTodoNotMarkedAsCompletedSpecification specification = mock(IsTodoNotMarkedAsCompletedSpecification.class);

        // When
        todoAggregateRoot.handle(new MarkTodoAsCompletedCommand("todoId"), specification);

        // Then
        verify(specification, times(1)).checkSatisfiedBy(todoAggregateRoot);
    }

}
