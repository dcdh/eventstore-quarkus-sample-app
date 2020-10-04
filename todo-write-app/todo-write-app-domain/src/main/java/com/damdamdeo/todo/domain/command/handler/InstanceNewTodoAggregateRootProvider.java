package com.damdamdeo.todo.domain.command.handler;

import com.damdamdeo.todo.domain.TodoAggregateRoot;

public class InstanceNewTodoAggregateRootProvider implements NewTodoAggregateRootProvider {

    @Override
    public TodoAggregateRoot create(final String todoId) {
        return new TodoAggregateRoot(todoId);
    }

}
