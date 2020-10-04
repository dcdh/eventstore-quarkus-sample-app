package com.damdamdeo.todo.domain;

import com.damdamdeo.todo.domain.api.TodoStatus;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TodoDomainTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(TodoDomain.class).verify();
    }

    @Test
    public void should_mark_todo_as_completed() {
        // Given
        final TodoDomain todoDomain = TodoDomain.newBuilder()
                .withTodoId("todoId")
                .withDescription("description")
                .withTodoStatus(TodoStatus.IN_PROGRESS)
                .withVersion(0l)
                .build();

        // When
        final TodoDomain todoMarkedAsCompleted = todoDomain.markAsCompleted(1l);

        // Then
        assertEquals(TodoDomain.newBuilder()
                .withTodoId("todoId")
                .withDescription("description")
                .withTodoStatus(TodoStatus.COMPLETED)
                .withVersion(1l)
                .build(), todoMarkedAsCompleted);
    }

}
