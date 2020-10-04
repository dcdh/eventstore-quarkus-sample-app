package com.damdamdeo.todo.domain.command.handler;

import com.damdamdeo.todo.domain.TodoAggregateRoot;
import com.damdamdeo.todo.domain.TodoAggregateRootRepository;
import com.damdamdeo.todo.domain.api.TodoAlreadyExistentException;
import com.damdamdeo.todo.domain.command.CreateNewTodoCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class DomainCreateNewTodoCommandHandlerTest {

    DomainCreateNewTodoCommandHandler domainCreateNewTodoCommandHandler;

    TodoAggregateRootRepository mockTodoAggregateRootRepository;

    TodoIdGenerator mockTodoIdGenerator;

    NewTodoAggregateRootProvider mockNewTodoAggregateRootProvider;

    @BeforeEach
    public void setup() {
        mockTodoAggregateRootRepository = mock(TodoAggregateRootRepository.class);
        mockTodoIdGenerator = mock(TodoIdGenerator.class);
        mockNewTodoAggregateRootProvider = mock(NewTodoAggregateRootProvider.class);
        domainCreateNewTodoCommandHandler = new DomainCreateNewTodoCommandHandler(mockTodoAggregateRootRepository,
                mockTodoIdGenerator, mockNewTodoAggregateRootProvider);
    }

    @Test
    public void should_create_new_todo() throws Throwable {
        // Given
        doReturn("todoId").when(mockTodoIdGenerator).generateTodoId();
        final CreateNewTodoCommand createNewTodoCommand = new CreateNewTodoCommand("lorem ipsum");
        final TodoAggregateRoot todoAggregateRoot = mock(TodoAggregateRoot.class);
        doReturn(todoAggregateRoot).when(mockNewTodoAggregateRootProvider).create("todoId");

        // When
        domainCreateNewTodoCommandHandler.execute(createNewTodoCommand);

        // Then
        verify(todoAggregateRoot, times(1)).handle(createNewTodoCommand, "todoId");
        verify(mockTodoIdGenerator, times(1)).generateTodoId();
        verify(mockNewTodoAggregateRootProvider, times(1)).create(any());
    }

    @Test
    public void should_consume_create_new_todo_command_throws_todo_already_existent_exception_when_todo_exists() {
        // Given
        doReturn("todoId").when(mockTodoIdGenerator).generateTodoId();
        doReturn(Boolean.TRUE).when(mockTodoAggregateRootRepository).isTodoExistent("todoId");
        final TodoAggregateRoot todoAggregateRoot = mock(TodoAggregateRoot.class);
        doReturn(todoAggregateRoot).when(mockTodoAggregateRootRepository).load("todoId");
        final CreateNewTodoCommand createNewTodoCommand = new CreateNewTodoCommand("lorem ipsum");

        // When && Then
        final TodoAlreadyExistentException todoAlreadyExistentException = assertThrows(TodoAlreadyExistentException.class,
                () -> domainCreateNewTodoCommandHandler.execute(createNewTodoCommand));
        assertEquals(new TodoAlreadyExistentException(todoAggregateRoot), todoAlreadyExistentException);

        verify(mockTodoIdGenerator, times(1)).generateTodoId();
        verify(mockTodoAggregateRootRepository, times(1)).isTodoExistent(anyString());
        verify(mockTodoAggregateRootRepository, times(1)).load(anyString());
        verifyNoMoreInteractions(mockTodoAggregateRootRepository, mockTodoIdGenerator, mockNewTodoAggregateRootProvider, todoAggregateRoot);
    }

    @Test
    public void should_consume_create_new_todo_command_be_handled_by_aggregate() throws Throwable {
        // Given
        doReturn("todoId").when(mockTodoIdGenerator).generateTodoId();
        doReturn(Boolean.FALSE).when(mockTodoAggregateRootRepository).isTodoExistent("todoId");
        final TodoAggregateRoot todoAggregateRoot = mock(TodoAggregateRoot.class);
        doReturn(todoAggregateRoot).when(mockNewTodoAggregateRootProvider).create("todoId");
        doReturn(todoAggregateRoot).when(mockTodoAggregateRootRepository).save(todoAggregateRoot);
        final CreateNewTodoCommand createNewTodoCommand = new CreateNewTodoCommand("lorem ipsum");

        // When
        final TodoAggregateRoot returnedTodoAggregateRoot = domainCreateNewTodoCommandHandler.execute(createNewTodoCommand);

        // Then
        assertEquals(todoAggregateRoot, returnedTodoAggregateRoot);
        verify(todoAggregateRoot, times(1)).handle(any(), any());
        verify(mockTodoAggregateRootRepository, times(1)).save(any(TodoAggregateRoot.class));
        verify(mockTodoIdGenerator, times(1)).generateTodoId();
        verify(mockTodoAggregateRootRepository, times(1)).isTodoExistent(any());
        verify(mockNewTodoAggregateRootProvider, times(1)).create(any());
        verifyNoMoreInteractions(mockTodoAggregateRootRepository, mockTodoIdGenerator, mockNewTodoAggregateRootProvider,
                todoAggregateRoot);
    }

}
