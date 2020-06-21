package com.damdamdeo.todo.domain.api;

import com.damdamdeo.todo.domain.api.shared.specification.Specification;
import com.damdamdeo.todo.domain.api.specification.IsTodoNotMarkedAsCompletedSpecification;

public interface Todo {

    String todoId();

    String description();

    TodoStatus todoStatus();

    Long version();

    default Specification<Todo> canMarkTodoAsCompletedSpecification() {
        return new IsTodoNotMarkedAsCompletedSpecification();
    }
}
