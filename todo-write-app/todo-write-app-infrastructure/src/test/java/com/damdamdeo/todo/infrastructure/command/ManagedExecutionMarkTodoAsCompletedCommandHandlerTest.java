package com.damdamdeo.todo.infrastructure.command;

import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.command.CommandExecutor;
import com.damdamdeo.todo.domain.command.MarkTodoAsCompletedCommand;
import com.damdamdeo.todo.domain.command.handler.MarkTodoAsCompletedCommandHandler;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.junit.mockito.InjectSpy;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class ManagedExecutionMarkTodoAsCompletedCommandHandlerTest {

    @InjectSpy
    CommandExecutor spyCommandExecutor;

    @InjectMock
    MarkTodoAsCompletedCommandHandler markTodoAsCompletedCommandHandler;

    @Inject
    ManagedExecutionMarkTodoAsCompletedCommandHandler managedExecutionMarkTodoAsCompletedCommandHandler;

    @Test
    public void should_call_domain_command_handler() throws Throwable {
        // Given
        final MarkTodoAsCompletedCommand markTodoAsCompletedCommand = new MarkTodoAsCompletedCommand("todoId");

        // When
        managedExecutionMarkTodoAsCompletedCommandHandler.execute(markTodoAsCompletedCommand);

        // Then
        verify(markTodoAsCompletedCommandHandler, times(1)).execute(markTodoAsCompletedCommand);
    }

    @Test
    public void should_use_executor_to_execute_command() throws Throwable {
        // Given
        final MarkTodoAsCompletedCommand markTodoAsCompletedCommand = new MarkTodoAsCompletedCommand("todoId");

        // When
        managedExecutionMarkTodoAsCompletedCommandHandler.execute(markTodoAsCompletedCommand);

        // Then
        verify(spyCommandExecutor, times(1)).execute(any());
    }

}
