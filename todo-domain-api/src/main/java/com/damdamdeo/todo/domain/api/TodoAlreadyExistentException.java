package com.damdamdeo.todo.domain.api;

import com.damdamdeo.todo.domain.api.shared.specification.UnsatisfiedSpecificationException;

import java.util.Objects;

public class TodoAlreadyExistentException extends UnsatisfiedSpecificationException {

    private final Todo todoAlreadyExistent;

    public TodoAlreadyExistentException(final Todo todoAlreadyExistent) {
        this.todoAlreadyExistent = Objects.requireNonNull(todoAlreadyExistent);
    }

    public Todo todoIdAlreadyExistent() {
        return todoAlreadyExistent;
    }

}
