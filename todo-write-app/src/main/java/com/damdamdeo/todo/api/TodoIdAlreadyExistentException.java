package com.damdamdeo.todo.api;

import java.util.Objects;

public class TodoIdAlreadyExistentException extends RuntimeException {

    private final String todoIdAlreadyExistent;

    public TodoIdAlreadyExistentException(final String todoIdAlreadyExistent) {
        this.todoIdAlreadyExistent = Objects.requireNonNull(todoIdAlreadyExistent);
    }

    public String todoIdAlreadyExistent() {
        return todoIdAlreadyExistent;
    }

}
