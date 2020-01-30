package com.damdamdeo.todo.domain.api;

import com.damdamdeo.todo.domain.api.shared.specification.UnsatisfiedSpecificationException;

import java.util.Objects;

public class TodoAlreadyMarkedAsCompletedException extends UnsatisfiedSpecificationException {

    private final Todo todoAlreadyMarkedAsCompleted;

    public TodoAlreadyMarkedAsCompletedException(final Todo todoAlreadyMarkedAsCompleted) {
        this.todoAlreadyMarkedAsCompleted = Objects.requireNonNull(todoAlreadyMarkedAsCompleted);
    }

    public Todo todoAlreadyMarkedAsCompleted() {
        return todoAlreadyMarkedAsCompleted;
    }

}
