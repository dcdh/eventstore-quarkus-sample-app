package com.damdamdeo.todo.infrastructure.command;

import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.command.CommandExecutor;
import com.damdamdeo.todo.domain.command.CreateNewTodoCommand;
import com.damdamdeo.todo.domain.command.handler.CreateNewTodoCommandHandler;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.junit.mockito.InjectSpy;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.inject.Named;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@QuarkusTest
public class SingleExecutionCreateNewTodoCommandHandlerTest {

    @InjectSpy
    CommandExecutor spyCommandExecutor;

    @InjectMock
    @Named("DomainCreateNewTodoCommandHandler")
    CreateNewTodoCommandHandler domainCreateNewTodoCommandHandler;

    @Inject
    SingleExecutionCreateNewTodoCommandHandler singleExecutionCreateNewTodoCommandHandler;

    @Test
    public void should_call_domain_command_handler() throws Throwable {
        // Given
        final CreateNewTodoCommand createNewTodoCommand = mock(CreateNewTodoCommand.class);

        // When
        singleExecutionCreateNewTodoCommandHandler.execute(createNewTodoCommand);

        // Then
        verify(domainCreateNewTodoCommandHandler, times(1)).execute(createNewTodoCommand);
    }

    @Test
    public void should_use_executor_to_execute_command() throws Throwable {
        // Given
        final CreateNewTodoCommand createNewTodoCommand = mock(CreateNewTodoCommand.class);

        // When
        singleExecutionCreateNewTodoCommandHandler.execute(createNewTodoCommand);

        // Then
        verify(spyCommandExecutor, times(1)).execute(any());
    }

}
