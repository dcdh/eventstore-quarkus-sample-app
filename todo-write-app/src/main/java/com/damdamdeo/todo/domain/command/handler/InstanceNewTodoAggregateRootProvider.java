package com.damdamdeo.todo.domain.command.handler;

import com.damdamdeo.todo.domain.TodoAggregateRoot;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InstanceNewTodoAggregateRootProvider implements NewTodoAggregateRootProvider {

    @Override
    public TodoAggregateRoot create(final String todoId) {
        return new TodoAggregateRoot(todoId);
    }

}
