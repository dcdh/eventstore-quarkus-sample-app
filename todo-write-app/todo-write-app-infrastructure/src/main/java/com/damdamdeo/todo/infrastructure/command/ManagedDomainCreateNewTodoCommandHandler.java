package com.damdamdeo.todo.infrastructure.command;

import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.command.CommandExecutorBinding;
import com.damdamdeo.todo.domain.TodoAggregateRoot;
import com.damdamdeo.todo.domain.command.CreateNewTodoCommand;
import com.damdamdeo.todo.domain.command.handler.CreateNewTodoCommandHandler;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class ManagedDomainCreateNewTodoCommandHandler {

    private final CreateNewTodoCommandHandler createNewTodoCommandHandler;

    public ManagedDomainCreateNewTodoCommandHandler(final CreateNewTodoCommandHandler createNewTodoCommandHandler) {
        this.createNewTodoCommandHandler = Objects.requireNonNull(createNewTodoCommandHandler);
    }

    @CommandExecutorBinding
    public TodoAggregateRoot execute(final CreateNewTodoCommand command) throws Throwable {
        return createNewTodoCommandHandler.execute(command);
    }

}
