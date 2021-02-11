package com.damdamdeo.todo.infrastructure;

import com.damdamdeo.todo.domain.TodoDomainRepository;
import com.damdamdeo.todo.domain.usecase.CreateTodoUseCase;
import com.damdamdeo.todo.domain.usecase.MarkTodoAsCompletedUseCase;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Objects;

public class DomainUseCaseProducers {

    private final TodoDomainRepository todoDomainRepository;

    public DomainUseCaseProducers(final TodoDomainRepository todoDomainRepository) {
        this.todoDomainRepository = Objects.requireNonNull(todoDomainRepository);
    }

    @Produces
    @ApplicationScoped
    public CreateTodoUseCase produceCreateTodoUseCase() {
        return new CreateTodoUseCase(todoDomainRepository);
    }

    @Produces
    @ApplicationScoped
    public MarkTodoAsCompletedUseCase produceMarkTodoAsCompletedUseCase() {
        return new MarkTodoAsCompletedUseCase(todoDomainRepository);
    }

}
