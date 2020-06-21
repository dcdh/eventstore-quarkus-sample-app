package com.damdamdeo.todo.domain.api;

import com.damdamdeo.todo.domain.api.shared.specification.UnsatisfiedSpecificationException;

import java.util.Objects;

public final class UnknownTodoException extends UnsatisfiedSpecificationException {

    private final String unknownTodoId;

    public UnknownTodoException(final String unknownTodoId) {
        this.unknownTodoId = Objects.requireNonNull(unknownTodoId);
    }

    public String unknownTodoId() {
        return unknownTodoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnknownTodoException that = (UnknownTodoException) o;
        return Objects.equals(unknownTodoId, that.unknownTodoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unknownTodoId);
    }
}
