package com.damdamdeo.todo.domain.api.specification;

import com.damdamdeo.todo.domain.api.Todo;
import com.damdamdeo.todo.domain.api.UnknownTodoException;
import com.damdamdeo.todo.domain.api.shared.specification.AbstractSpecification;
import com.damdamdeo.todo.domain.api.shared.specification.UnsatisfiedSpecificationException;

public class IsTodoExistentSpecification extends AbstractSpecification<Todo> {

    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        return todo.todoId() != null;
    }

    @Override
    public UnsatisfiedSpecificationException createUnsatisfiedSpecificationException(final Todo todo) {
        return new UnknownTodoException(todo);
    }

}
