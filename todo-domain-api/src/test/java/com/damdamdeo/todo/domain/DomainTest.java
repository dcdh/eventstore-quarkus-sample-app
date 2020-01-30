package com.damdamdeo.todo.domain;

import com.damdamdeo.todo.domain.api.Todo;
import com.damdamdeo.todo.domain.api.TodoStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class DomainTest {

    @Test
    public void should_be_able_to_mark_event_as_completed_when_event_is_in_progress() {
        // Given
        final Todo todo = new TodoDomain("todoId", "lorem ipsum", TodoStatus.IN_PROGRESS, 0L);

        // When
        final Boolean canMarkTodoAsCompleted = todo.canMarkTodoAsCompletedSpecification().isSatisfiedBy(todo);

        // Then
        assertTrue(canMarkTodoAsCompleted);
    }

    @Test
    public void should_not_be_able_to_mark_event_as_completed_when_event_does_not_exist() {
        // Given
        final Todo todo = new TodoDomain(null, null, null, null);

        // When
        final Boolean canMarkTodoAsCompleted = todo.canMarkTodoAsCompletedSpecification().isSatisfiedBy(todo);

        // Then
        assertFalse(canMarkTodoAsCompleted);
    }

    @Test
    public void should_not_be_able_to_mark_event_as_completed_when_event_is_completed() {
        // Given
        final Todo todo = new TodoDomain("todoId", "lorem ipsum", TodoStatus.COMPLETED, 0L);

        // When
        final Boolean canMarkTodoAsCompleted = todo.canMarkTodoAsCompletedSpecification().isSatisfiedBy(todo);

        // Then
        assertFalse(canMarkTodoAsCompleted);
    }

}
