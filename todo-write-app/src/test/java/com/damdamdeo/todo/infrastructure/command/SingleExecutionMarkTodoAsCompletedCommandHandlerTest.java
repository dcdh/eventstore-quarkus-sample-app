package com.damdamdeo.todo.infrastructure.command;

import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.command.CommandExecutor;
import com.damdamdeo.todo.domain.command.MarkTodoAsCompletedCommand;
import com.damdamdeo.todo.domain.command.handler.MarkTodoAsCompletedCommandHandler;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.junit.mockito.InjectSpy;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.inject.Named;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class SingleExecutionMarkTodoAsCompletedCommandHandlerTest {

    @InjectSpy
    CommandExecutor spyCommandExecutor;

    @InjectMock
    @Named("DomainMarkTodoAsCompletedCommandHandler")
    MarkTodoAsCompletedCommandHandler domainMarkTodoAsCompletedCommandHandler;

    @Inject
    SingleExecutionMarkTodoAsCompletedCommandHandler singleExecutionMarkTodoAsCompletedCommandHandler;

    @Test
    public void should_call_domain_command_handler() throws Throwable {
        // Given
        final MarkTodoAsCompletedCommand markTodoAsCompletedCommand = mock(MarkTodoAsCompletedCommand.class);

        // When
        singleExecutionMarkTodoAsCompletedCommandHandler.execute(markTodoAsCompletedCommand);

        // Then
        verify(domainMarkTodoAsCompletedCommandHandler, times(1)).execute(markTodoAsCompletedCommand);
    }

    @Test
    public void should_use_executor_to_execute_command() throws Throwable {
        // Given
        final MarkTodoAsCompletedCommand markTodoAsCompletedCommand = mock(MarkTodoAsCompletedCommand.class);

        // When
        singleExecutionMarkTodoAsCompletedCommandHandler.execute(markTodoAsCompletedCommand);

        // Then
        verify(spyCommandExecutor, times(1)).execute(any());
    }

}
