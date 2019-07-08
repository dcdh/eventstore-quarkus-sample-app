package com.damdamdeo.todo.domain;

import com.damdamdeo.todo.api.Todo;
import com.damdamdeo.todo.api.TodoAggregateRootRepository;

public interface TodoCommand {

    String todoId();

    boolean exactlyOnceCommandExecution();

    Todo handle(TodoAggregateRootRepository todoAggregateRootRepository);

}
