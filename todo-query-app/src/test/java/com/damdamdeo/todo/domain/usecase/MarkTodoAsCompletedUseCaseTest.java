package com.damdamdeo.todo.domain.usecase;

import com.damdamdeo.todo.domain.TodoDomain;
import com.damdamdeo.todo.domain.TodoDomainRepository;
import com.damdamdeo.todo.domain.api.TodoStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class MarkTodoAsCompletedUseCaseTest {

    @Test
    public void should_load_todo() {
        // Given
        final TodoDomainRepository todoDomainRepository = mock(TodoDomainRepository.class);
        final MarkTodoAsCompletedUseCase markTodoAsCompletedUseCase = new MarkTodoAsCompletedUseCase(todoDomainRepository);
        final TodoDomain todoDomain = TodoDomain.newBuilder()
                .withTodoId("todoId")
                .withDescription("description")
                .withTodoStatus(TodoStatus.IN_PROGRESS)
                .withVersion(0l)
                .build();
        doReturn(todoDomain).when(todoDomainRepository).get("todoId");

        // When
        markTodoAsCompletedUseCase.execute(new MarkTodoAsCompletedCommand("todoId", 1l));

        // Then
        verify(todoDomainRepository, times(1)).get("todoId");
    }

    @Test
    public void should_merge_todo_marked_as_completed() {
        // Given
        final TodoDomainRepository todoDomainRepository = mock(TodoDomainRepository.class);
        final MarkTodoAsCompletedUseCase markTodoAsCompletedUseCase = new MarkTodoAsCompletedUseCase(todoDomainRepository);
        final TodoDomain todoDomain = TodoDomain.newBuilder()
                .withTodoId("todoId")
                .withDescription("description")
                .withTodoStatus(TodoStatus.IN_PROGRESS)
                .withVersion(0l)
                .build();
        doReturn(todoDomain).when(todoDomainRepository).get("todoId");

        // When
        markTodoAsCompletedUseCase.execute(new MarkTodoAsCompletedCommand("todoId", 1l));

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
