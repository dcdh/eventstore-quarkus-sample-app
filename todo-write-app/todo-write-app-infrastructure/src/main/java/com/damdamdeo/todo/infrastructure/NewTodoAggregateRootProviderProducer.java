package com.damdamdeo.todo.infrastructure;

import com.damdamdeo.todo.domain.command.handler.InstanceNewTodoAggregateRootProvider;
import com.damdamdeo.todo.domain.command.handler.NewTodoAggregateRootProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

public class NewTodoAggregateRootProviderProducer {

    @Produces
    @ApplicationScoped
    public NewTodoAggregateRootProvider newTodoAggregateRootProviderProducer() {
        return new InstanceNewTodoAggregateRootProvider();
    }

}
