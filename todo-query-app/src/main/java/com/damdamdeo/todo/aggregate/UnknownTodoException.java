package com.damdamdeo.todo.aggregate;

public class UnknownTodoException extends RuntimeException {

    private final String unknownTodoId;

    public UnknownTodoException(final String unknownTodoId) {
        this.unknownTodoId = unknownTodoId;
    }

    public String unknownTodoId() {
        return unknownTodoId;
    }

}
