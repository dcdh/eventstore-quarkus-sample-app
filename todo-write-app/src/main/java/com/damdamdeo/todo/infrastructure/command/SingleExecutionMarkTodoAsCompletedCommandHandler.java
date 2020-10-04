package com.damdamdeo.todo.infrastructure.command;

import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.command.CommandExecutorBinding;
import com.damdamdeo.todo.domain.TodoAggregateRoot;
import com.damdamdeo.todo.domain.command.MarkTodoAsCompletedCommand;
import com.damdamdeo.todo.domain.command.handler.MarkTodoAsCompletedCommandHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.Objects;

@Named("SingleExecutionMarkTodoAsCompletedCommandHandler")
@ApplicationScoped
public class SingleExecutionMarkTodoAsCompletedCommandHandler implements MarkTodoAsCompletedCommandHandler {

    private final MarkTodoAsCompletedCommandHandler domainMarkTodoAsCompletedCommandHandler;

    public SingleExecutionMarkTodoAsCompletedCommandHandler(@Named("DomainMarkTodoAsCompletedCommandHandler") final MarkTodoAsCompletedCommandHandler domainMarkTodoAsCompletedCommandHandler) {
        this.domainMarkTodoAsCompletedCommandHandler = Objects.requireNonNull(domainMarkTodoAsCompletedCommandHandler);
    }

    @CommandExecutorBinding
    @Override
    public TodoAggregateRoot execute(final MarkTodoAsCompletedCommand command) throws Throwable {
        return domainMarkTodoAsCompletedCommandHandler.execute(command);
    }

}
