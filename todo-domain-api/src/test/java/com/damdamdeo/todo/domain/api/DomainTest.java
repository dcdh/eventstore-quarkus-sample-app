package com.damdamdeo.todo.domain.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class DomainTest {

    @Test
    public void should_be_able_to_mark_event_as_completed_when_event_is_in_progress() {
        // Given
        final Todo todo = new TodoDomain("todoId", "lorem ipsum", TodoStatus.IN_PROGRESS, 0L);

        // When
        final Boolean canMarkTodoAsCompleted = todo.canMarkTodoAsCompleted();

        // Then
        assertTrue(canMarkTodoAsCompleted);
    }

    @Test
    public void should_not_be_able_to_mark_event_as_completed_when_event_is_completed() {
        // Given
        final Todo todo = new TodoDomain("todoId", "lorem ipsum", TodoStatus.COMPLETED, 0L);

        // When
        final Boolean canMarkTodoAsCompleted = todo.canMarkTodoAsCompleted();

        // Then
        assertFalse(canMarkTodoAsCompleted);
    }

}
