package com.damdamdeo.todo.domain.api;

import com.damdamdeo.todo.domain.api.shared.specification.UnsatisfiedSpecificationException;

import java.util.Objects;

public final class TodoAlreadyExistentException extends UnsatisfiedSpecificationException {

    private final Todo todoAlreadyExistent;

    public TodoAlreadyExistentException(final Todo todoAlreadyExistent) {
        this.todoAlreadyExistent = Objects.requireNonNull(todoAlreadyExistent);
    }

    public Todo todoIdAlreadyExistent() {
        return todoAlreadyExistent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoAlreadyExistentException that = (TodoAlreadyExistentException) o;
        return Objects.equals(todoAlreadyExistent, that.todoAlreadyExistent);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoAlreadyExistent);
    }
}
