package com.damdamdeo.todo.domain;

import com.damdamdeo.todo.domain.api.TodoStatus;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class DomainMarkTodoAsCompletedServiceTest {

    @Test
    public void should_load_todo() {
        // Given
        final TodoDomainRepository todoDomainRepository = mock(TodoDomainRepository.class);
        final DomainMarkTodoAsCompletedService domainMarkTodoAsCompletedService = new DomainMarkTodoAsCompletedService(todoDomainRepository);
        final TodoDomain todoDomain = TodoDomain.newBuilder()
                .withTodoId("todoId")
                .withDescription("description")
                .withTodoStatus(TodoStatus.IN_PROGRESS)
                .withVersion(0l)
                .build();
        doReturn(todoDomain).when(todoDomainRepository).get("todoId");

        // When
        domainMarkTodoAsCompletedService.markTodoAsCompleted("todoId", 1l);

        // Then
        verify(todoDomainRepository, times(1)).get("todoId");
    }

    @Test
    public void should_merge_todo_marked_as_completed() {
        // Given
        final TodoDomainRepository todoDomainRepository = mock(TodoDomainRepository.class);
        final DomainMarkTodoAsCompletedService domainMarkTodoAsCompletedService = new DomainMarkTodoAsCompletedService(todoDomainRepository);
        final TodoDomain todoDomain = TodoDomain.newBuilder()
                .withTodoId("todoId")
                .withDescription("description")
                .withTodoStatus(TodoStatus.IN_PROGRESS)
                .withVersion(0l)
                .build();
        doReturn(todoDomain).when(todoDomainRepository).get("todoId");

        // When
        domainMarkTodoAsCompletedService.markTodoAsCompleted("todoId", 1l);

        // Then
        verify(todoDomainRepository, times(1)).merge(TodoDomain.newBuilder()
                .withTodoId("todoId")
                .withDescription("description")
                .withTodoStatus(TodoStatus.COMPLETED)
                .withVersion(1l)
                .build());

        verify(todoDomainRepository, times(1)).get(any());
    }

}
