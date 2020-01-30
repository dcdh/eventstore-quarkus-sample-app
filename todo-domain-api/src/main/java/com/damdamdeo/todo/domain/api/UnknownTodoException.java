package com.damdamdeo.todo.domain.api;

import com.damdamdeo.todo.domain.api.shared.specification.UnsatisfiedSpecificationException;

import java.util.Objects;

public class UnknownTodoException extends UnsatisfiedSpecificationException {

    private final Todo unknownTodo;

    public UnknownTodoException(final Todo unknownTodo) {
        this.unknownTodo = Objects.requireNonNull(unknownTodo);
    }

    public Todo unknownTodoId() {
        return unknownTodo;
    }

}
