package com.damdamdeo.todo.command.handler;

import com.damdamdeo.todo.aggregate.TodoAggregateRoot;

public interface NewTodoAggregateRootProvider {

    TodoAggregateRoot create(String todoId);

}
