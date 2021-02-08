package com.damdamdeo.todo.domain.api;

import com.damdamdeo.todo.domain.api.specification.IsTodoNotMarkedAsCompletedSpecification;

public interface Todo {

    String todoId();

    String description();

    TodoStatus todoStatus();

    Long version();

    default Boolean canMarkTodoAsCompleted() {
        return new IsTodoNotMarkedAsCompletedSpecification().isSatisfiedBy(this);
    }
}
