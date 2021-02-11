package com.damdamdeo.todo.domain.usecase;

import com.damdamdeo.todo.domain.TodoDomain;
import com.damdamdeo.todo.domain.TodoDomainRepository;
import com.damdamdeo.todo.domain.api.TodoStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateTodoUseCaseTest {

    @Test
    public void should_create_todo() {
        // Given
        final TodoDomainRepository todoDomainRepository = mock(TodoDomainRepository.class);
        final CreateTodoUseCase createTodoUseCase = new CreateTodoUseCase(todoDomainRepository);

        // When
        createTodoUseCase.execute(new CreateTodoCommand("todoId", "description", 0l));

        // Then
        verify(todoDomainRepository, times(1)).persist(TodoDomain.newBuilder()
                .withTodoId("todoId")
                .withDescription("description")
                .withTodoStatus(TodoStatus.IN_PROGRESS)
                .withVersion(0l)
                .build());
    }

}
