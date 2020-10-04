package com.damdamdeo.todo.domain.command.handler;

import com.damdamdeo.todo.domain.TodoAggregateRoot;

public interface NewTodoAggregateRootProvider {

    TodoAggregateRoot create(String todoId);

}
