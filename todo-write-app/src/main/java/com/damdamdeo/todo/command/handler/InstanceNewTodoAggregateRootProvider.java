package com.damdamdeo.todo.command.handler;

import com.damdamdeo.todo.aggregate.TodoAggregateRoot;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class InstanceNewTodoAggregateRootProvider implements NewTodoAggregateRootProvider {

    @Override
    public TodoAggregateRoot create(final String todoId) {
        return new TodoAggregateRoot(todoId);
    }

}
