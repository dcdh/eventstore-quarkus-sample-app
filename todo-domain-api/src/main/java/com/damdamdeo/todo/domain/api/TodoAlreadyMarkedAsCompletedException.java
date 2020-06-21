package com.damdamdeo.todo.domain.api;

import com.damdamdeo.todo.domain.api.shared.specification.UnsatisfiedSpecificationException;

import java.util.Objects;

public final class TodoAlreadyMarkedAsCompletedException extends UnsatisfiedSpecificationException {

    private final Todo todoAlreadyMarkedAsCompleted;

    public TodoAlreadyMarkedAsCompletedException(final Todo todoAlreadyMarkedAsCompleted) {
        this.todoAlreadyMarkedAsCompleted = Objects.requireNonNull(todoAlreadyMarkedAsCompleted);
    }

    public Todo todoAlreadyMarkedAsCompleted() {
        return todoAlreadyMarkedAsCompleted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoAlreadyMarkedAsCompletedException that = (TodoAlreadyMarkedAsCompletedException) o;
        return Objects.equals(todoAlreadyMarkedAsCompleted, that.todoAlreadyMarkedAsCompleted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoAlreadyMarkedAsCompleted);
    }
}
