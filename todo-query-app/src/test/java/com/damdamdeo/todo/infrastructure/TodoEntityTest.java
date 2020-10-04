package com.damdamdeo.todo.infrastructure;

import com.damdamdeo.todo.domain.TodoDomain;
import com.damdamdeo.todo.domain.api.TodoStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TodoEntityTest {

    @Test
    public void should_build_entity() {
        // Given
        final TodoEntity todoEntity = TodoEntity.newBuilder()
                .withTodoId("todoId")
                .withDescription("description")
                .withTodoStatus(TodoStatus.IN_PROGRESS)
                .withVersion(0l).build();

        // When

        // Then
        assertEquals("todoId", todoEntity.todoId());
        assertEquals("description", todoEntity.description());
        assertEquals(TodoStatus.IN_PROGRESS, todoEntity.todoStatus());
        assertEquals(0l, todoEntity.version());
    }

    @Test
    public void should_map_entity_to_domain() {
        // Given
        final TodoEntity todoEntity = TodoEntity.newBuilder()
                .withTodoId("todoId")
                .withDescription("description")
                .withTodoStatus(TodoStatus.IN_PROGRESS)
                .withVersion(0l).build();

        // When
        final TodoDomain todoDomain = todoEntity.toDomain();

        // Then
        assertEquals(TodoDomain.newBuilder()
                        .withTodoId("todoId")
                        .withDescription("description")
                        .withTodoStatus(TodoStatus.IN_PROGRESS)
                        .withVersion(0l).build(),
                todoDomain);
    }

}
