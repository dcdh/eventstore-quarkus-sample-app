package com.damdamdeo.todo.infrastructure;

import com.damdamdeo.todo.domain.TodoAggregateRootRepository;
import com.damdamdeo.todo.domain.api.specification.IsTodoNotMarkedAsCompletedSpecification;
import com.damdamdeo.todo.domain.command.handler.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Objects;

public class DomainCommandHandlerProducers {

    private final TodoAggregateRootRepository todoAggregateRootRepository;
    private final TodoIdGenerator todoIdGenerator;
    private final NewTodoAggregateRootProvider newTodoAggregateRootProvider;

    public DomainCommandHandlerProducers(final TodoAggregateRootRepository todoAggregateRootRepository,
                                         final TodoIdGenerator todoIdGenerator,
                                         final NewTodoAggregateRootProvider newTodoAggregateRootProvider) {
        this.todoAggregateRootRepository = Objects.requireNonNull(todoAggregateRootRepository);
        this.todoIdGenerator = Objects.requireNonNull(todoIdGenerator);
        this.newTodoAggregateRootProvider = Objects.requireNonNull(newTodoAggregateRootProvider);
    }

    @Produces
    @ApplicationScoped
    public DomainCreateNewTodoCommandHandler createNewTodoCommandHandler() {
        return new DomainCreateNewTodoCommandHandler(todoAggregateRootRepository, todoIdGenerator, newTodoAggregateRootProvider);
    }

    @Produces
    @ApplicationScoped
    public DomainMarkTodoAsCompletedCommandHandler markTodoAsCompletedCommandHandler() {
        return new DomainMarkTodoAsCompletedCommandHandler(todoAggregateRootRepository,
                new IsTodoNotMarkedAsCompletedSpecification());
    }

}
