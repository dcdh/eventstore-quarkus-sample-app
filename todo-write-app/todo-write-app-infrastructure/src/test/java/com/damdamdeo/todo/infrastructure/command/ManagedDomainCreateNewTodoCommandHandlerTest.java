package com.damdamdeo.todo.infrastructure.command;

import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.command.CommandExecutor;
import com.damdamdeo.todo.domain.command.CreateNewTodoCommand;
import com.damdamdeo.todo.domain.command.handler.CreateNewTodoCommandHandler;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.junit.mockito.InjectSpy;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class ManagedDomainCreateNewTodoCommandHandlerTest {

    @InjectSpy
    CommandExecutor spyCommandExecutor;

    @InjectMock
    CreateNewTodoCommandHandler createNewTodoCommandHandler;

    @Inject
    ManagedDomainCreateNewTodoCommandHandler managedDomainCreateNewTodoCommandHandler;

    @Test
    public void should_call_domain_command_handler() throws Throwable {
        // Given
        final CreateNewTodoCommand createNewTodoCommand = new CreateNewTodoCommand("description");

        // When
        managedDomainCreateNewTodoCommandHandler.execute(createNewTodoCommand);

        // Then
        verify(createNewTodoCommandHandler, times(1)).execute(createNewTodoCommand);
    }

    @Test
    public void should_use_executor_to_execute_command() throws Throwable {
        // Given
        final CreateNewTodoCommand createNewTodoCommand = new CreateNewTodoCommand("description");

        // When
        managedDomainCreateNewTodoCommandHandler.execute(createNewTodoCommand);

        // Then
        verify(spyCommandExecutor, times(1)).execute(any());
    }

}
