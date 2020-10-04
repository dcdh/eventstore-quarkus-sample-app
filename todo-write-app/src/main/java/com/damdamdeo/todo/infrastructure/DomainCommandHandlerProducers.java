package com.damdamdeo.todo.infrastructure;

import com.damdamdeo.todo.domain.TodoAggregateRootRepository;
import com.damdamdeo.todo.domain.command.handler.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
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
    @Named("DomainCreateNewTodoCommandHandler")
    @ApplicationScoped
    public CreateNewTodoCommandHandler domainCreateNewTodoCommandHandlerProducer() {
        return new DomainCreateNewTodoCommandHandler(todoAggregateRootRepository, todoIdGenerator, newTodoAggregateRootProvider);
    }

    @Produces
    @Named("DomainMarkTodoAsCompletedCommandHandler")
    @ApplicationScoped
    public MarkTodoAsCompletedCommandHandler domainMarkTodoAsCompletedCommandHandlerProducer() {
        return new DomainMarkTodoAsCompletedCommandHandler(todoAggregateRootRepository);
    }

}
