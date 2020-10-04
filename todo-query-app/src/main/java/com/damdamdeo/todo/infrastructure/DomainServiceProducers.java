package com.damdamdeo.todo.infrastructure;

import com.damdamdeo.todo.domain.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

public class DomainServiceProducers {

    private final TodoDomainRepository todoDomainRepository;

    public DomainServiceProducers(final TodoDomainRepository todoDomainRepository) {
        this.todoDomainRepository = todoDomainRepository;
    }

    @Produces
    @ApplicationScoped
    public CreateTodoService createTodoServiceProducer() {
        return new DomainCreateTodoService(todoDomainRepository);
    }

    @Produces
    @ApplicationScoped
    public MarkTodoAsCompletedService markTodoAsCompletedServiceProducer() {
        return new DomainMarkTodoAsCompletedService(todoDomainRepository);
    }

}
