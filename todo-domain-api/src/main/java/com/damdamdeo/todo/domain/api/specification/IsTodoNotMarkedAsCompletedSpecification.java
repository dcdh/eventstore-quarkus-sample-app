package com.damdamdeo.todo.domain.api.specification;

import com.damdamdeo.todo.domain.api.Todo;
import com.damdamdeo.todo.domain.api.TodoAlreadyMarkedAsCompletedException;
import com.damdamdeo.todo.domain.api.TodoStatus;
import com.damdamdeo.todo.domain.api.shared.specification.AbstractSpecification;
import com.damdamdeo.todo.domain.api.shared.specification.UnsatisfiedSpecificationException;

public class IsTodoNotMarkedAsCompletedSpecification extends AbstractSpecification<Todo> {

    @Override
    public boolean isSatisfiedBy(final Todo todo) {
        return !TodoStatus.COMPLETED.equals(todo.todoStatus());
    }

    @Override
    public UnsatisfiedSpecificationException createUnsatisfiedSpecificationException(final Todo todo) {
        return new TodoAlreadyMarkedAsCompletedException(todo);
    }

}
