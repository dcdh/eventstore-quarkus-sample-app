package com.damdamdeo.todo.domain;

public class UnknownTodoException extends RuntimeException {

    private final String unknownTodoId;

    public UnknownTodoException(final String unknownTodoId) {
        this.unknownTodoId = unknownTodoId;
    }

    public String unknownTodoId() {
        return unknownTodoId;
    }

}
