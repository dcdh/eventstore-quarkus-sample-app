package com.damdamdeo.todo.infrastructure;

import com.damdamdeo.todo.domain.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

public class DomainServiceProducers {

    @Inject
    TodoDomainRepository todoDomainRepository;

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
