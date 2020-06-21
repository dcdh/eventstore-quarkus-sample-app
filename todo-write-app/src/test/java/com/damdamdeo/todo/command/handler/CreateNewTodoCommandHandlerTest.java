package com.damdamdeo.todo.command.handler;

import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.command.CommandExecutor;
import com.damdamdeo.todo.aggregate.TodoAggregateRoot;
import com.damdamdeo.todo.aggregate.TodoAggregateRootRepository;
import com.damdamdeo.todo.command.CreateNewTodoCommand;
import com.damdamdeo.todo.domain.api.TodoAlreadyExistentException;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.junit.mockito.InjectSpy;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@QuarkusTest
public class CreateNewTodoCommandHandlerTest {

    @Inject
    CreateNewTodoCommandHandler createNewTodoCommandHandler;

    @InjectMock
    TodoAggregateRootRepository mockTodoAggregateRootRepository;

    @InjectMock
    TodoIdGenerator mockTodoIdGenerator;

    @InjectMock
    NewTodoAggregateRootProvider mockNewTodoAggregateRootProvider;

    @InjectSpy
    CommandExecutor spyCommandExecutor;

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
                () -> createNewTodoCommandHandler.execute(createNewTodoCommand));
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
        doReturn(todoAggregateRoot).when(mockNewTodoAggregateRootProvider).create();
        doReturn(todoAggregateRoot).when(mockTodoAggregateRootRepository).save(todoAggregateRoot);
        final CreateNewTodoCommand createNewTodoCommand = new CreateNewTodoCommand("lorem ipsum");

        // When
        final TodoAggregateRoot returnedTodoAggregateRoot = createNewTodoCommandHandler.execute(createNewTodoCommand);

        // Then
        assertEquals(todoAggregateRoot, returnedTodoAggregateRoot);
        verify(todoAggregateRoot, times(1)).handle(any(), any());
        verify(mockTodoAggregateRootRepository, times(1)).save(any(TodoAggregateRoot.class));
        verify(mockTodoIdGenerator, times(1)).generateTodoId();
        verify(mockTodoAggregateRootRepository, times(1)).isTodoExistent(any());
        verify(mockNewTodoAggregateRootProvider, times(1)).create();
        verifyNoMoreInteractions(mockTodoAggregateRootRepository, mockTodoIdGenerator, mockNewTodoAggregateRootProvider,
                todoAggregateRoot);
    }

    @Test
    public void should_use_executor_to_execute_command() throws Throwable {
        // Given
        final CreateNewTodoCommand createNewTodoCommand = new CreateNewTodoCommand("lorem ipsum");
        doReturn(mock(TodoAggregateRoot.class)).when(mockNewTodoAggregateRootProvider).create();

        // When
        createNewTodoCommandHandler.execute(createNewTodoCommand);

        // Then
        verify(spyCommandExecutor, times(1)).execute(any());
        verify(mockNewTodoAggregateRootProvider, times(1)).create();
    }

}
