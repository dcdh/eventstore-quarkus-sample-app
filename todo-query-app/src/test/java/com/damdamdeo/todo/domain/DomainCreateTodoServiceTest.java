package com.damdamdeo.todo.domain;

import com.damdamdeo.todo.domain.api.TodoStatus;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class DomainCreateTodoServiceTest {

    @Test
    public void should_create_todo() {
        // Given
        final TodoDomainRepository todoDomainRepository = mock(TodoDomainRepository.class);
        final DomainCreateTodoService domainCreateTodoService = new DomainCreateTodoService(todoDomainRepository);

        // When
        domainCreateTodoService.createTodo("todoId", "description", 0l);

        // Then
        verify(todoDomainRepository, times(1)).persist(TodoDomain.newBuilder()
                .withTodoId("todoId")
                .withDescription("description")
                .withTodoStatus(TodoStatus.IN_PROGRESS)
                .withVersion(0l)
                .build());
    }

}
