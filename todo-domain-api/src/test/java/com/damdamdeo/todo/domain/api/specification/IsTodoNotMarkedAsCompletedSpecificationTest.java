package com.damdamdeo.todo.domain.api.specification;

import com.damdamdeo.todo.domain.api.Todo;
import com.damdamdeo.todo.domain.api.TodoAlreadyMarkedAsCompletedException;
import com.damdamdeo.todo.domain.api.TodoStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class IsTodoNotMarkedAsCompletedSpecificationTest {

    @Test
    public void should_be_satisfied_when_todo_is_in_progress() {
        // Given
        final Todo todo = mock(Todo.class);
        doReturn(TodoStatus.IN_PROGRESS).when(todo).todoStatus();
        final IsTodoNotMarkedAsCompletedSpecification isTodoNotMarkedAsCompletedSpecification = new IsTodoNotMarkedAsCompletedSpecification();

        // When
        final Boolean isTodoNotMarkedAsCompleted = isTodoNotMarkedAsCompletedSpecification.isSatisfiedBy(todo);

        // Then
        assertTrue(isTodoNotMarkedAsCompleted);
        verify(todo).todoStatus();
    }

    @Test
    public void should_not_be_satisfied_when_todo_is_completed() {
        // Given
        final Todo todo = mock(Todo.class);
        doReturn(TodoStatus.COMPLETED).when(todo).todoStatus();
        final IsTodoNotMarkedAsCompletedSpecification isTodoNotMarkedAsCompletedSpecification = new IsTodoNotMarkedAsCompletedSpecification();

        // When
        final Boolean isTodoNotMarkedAsCompleted = isTodoNotMarkedAsCompletedSpecification.isSatisfiedBy(todo);

        // Then
        assertFalse(isTodoNotMarkedAsCompleted);
        verify(todo).todoStatus();
    }

    @Test
    public void should_throw_TodoAlreadyMarkedAsCompletedException_when_todo_is_completed() {
        // Given
        final Todo todo = mock(Todo.class);
        doReturn(TodoStatus.COMPLETED).when(todo).todoStatus();
        final IsTodoNotMarkedAsCompletedSpecification isTodoNotMarkedAsCompleted = new IsTodoNotMarkedAsCompletedSpecification();

        // When && Then
        assertThrows(TodoAlreadyMarkedAsCompletedException.class, () -> isTodoNotMarkedAsCompleted.checkSatisfiedBy(todo));
        verify(todo).todoStatus();
    }

}
