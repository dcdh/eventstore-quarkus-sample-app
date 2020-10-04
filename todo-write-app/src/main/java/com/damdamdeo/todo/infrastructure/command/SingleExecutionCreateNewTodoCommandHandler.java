package com.damdamdeo.todo.infrastructure.command;

import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.command.CommandExecutorBinding;
import com.damdamdeo.todo.domain.TodoAggregateRoot;
import com.damdamdeo.todo.domain.command.CreateNewTodoCommand;
import com.damdamdeo.todo.domain.command.handler.CreateNewTodoCommandHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.Objects;

@Named("SingleExecutionCreateNewTodoCommandHandler")
@ApplicationScoped
public class SingleExecutionCreateNewTodoCommandHandler implements CreateNewTodoCommandHandler {

    private final CreateNewTodoCommandHandler domainCreateNewTodoCommandHandler;

    public SingleExecutionCreateNewTodoCommandHandler(@Named("DomainCreateNewTodoCommandHandler") final CreateNewTodoCommandHandler domainCreateNewTodoCommandHandler) {
        this.domainCreateNewTodoCommandHandler = Objects.requireNonNull(domainCreateNewTodoCommandHandler);
    }

    @CommandExecutorBinding
    @Override
    public TodoAggregateRoot execute(final CreateNewTodoCommand command) throws Throwable {
        return domainCreateNewTodoCommandHandler.execute(command);
    }

}
