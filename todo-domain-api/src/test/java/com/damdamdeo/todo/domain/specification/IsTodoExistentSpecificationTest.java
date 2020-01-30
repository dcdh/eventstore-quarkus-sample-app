package com.damdamdeo.todo.domain.specification;

import com.damdamdeo.todo.domain.api.Todo;
import com.damdamdeo.todo.domain.api.UnknownTodoException;
import com.damdamdeo.todo.domain.api.specification.IsTodoExistentSpecification;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class IsTodoExistentSpecificationTest {

    @Test
    public void should_be_satisfied_when_todo_exist() {
        // Given
        final Todo todo = mock(Todo.class);
        doReturn("todoId").when(todo).todoId();
        final IsTodoExistentSpecification isTodoExistentSpecification = new IsTodoExistentSpecification();

        // When
        final Boolean isTodoExistent = isTodoExistentSpecification.isSatisfiedBy(todo);

        // Then
        assertTrue(isTodoExistent);
        verify(todo).todoId();
    }

    @Test
    public void should_not_be_satisfied_when_todo_does_not_exist() {
        // Given
        final Todo todo = mock(Todo.class);
        doReturn(null).when(todo).todoId();
        final IsTodoExistentSpecification isTodoExistentSpecification = new IsTodoExistentSpecification();

        // When
        final Boolean isTodoExistent = isTodoExistentSpecification.isSatisfiedBy(todo);

        // Then
        assertFalse(isTodoExistent);
        verify(todo).todoId();
    }

    @Test
    public void should_throw_UnknownTodoException_when_todo_does_not_exist() {
        // Given
        final Todo todo = mock(Todo.class);
        doReturn(null).when(todo).todoId();
        final IsTodoExistentSpecification isTodoExistentSpecification = new IsTodoExistentSpecification();

        // When && Then
        assertThrows(UnknownTodoException.class, () -> isTodoExistentSpecification.checkSatisfiedBy(todo));
        verify(todo).todoId();
    }

}
