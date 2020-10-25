package com.damdamdeo.todo.infrastructure.command;

import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.command.CommandExecutorBinding;
import com.damdamdeo.todo.domain.TodoAggregateRoot;
import com.damdamdeo.todo.domain.command.MarkTodoAsCompletedCommand;
import com.damdamdeo.todo.domain.command.handler.MarkTodoAsCompletedCommandHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class ManagedExecutionMarkTodoAsCompletedCommandHandler {

    private final MarkTodoAsCompletedCommandHandler markTodoAsCompletedCommandHandler;

    public ManagedExecutionMarkTodoAsCompletedCommandHandler(final MarkTodoAsCompletedCommandHandler markTodoAsCompletedCommandHandler) {
        this.markTodoAsCompletedCommandHandler = Objects.requireNonNull(markTodoAsCompletedCommandHandler);
    }

    @CommandExecutorBinding
    public TodoAggregateRoot execute(final MarkTodoAsCompletedCommand command) throws Throwable {
        return markTodoAsCompletedCommandHandler.execute(command);
    }

}
