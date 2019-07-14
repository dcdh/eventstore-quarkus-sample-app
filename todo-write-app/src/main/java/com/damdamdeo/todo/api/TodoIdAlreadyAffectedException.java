package com.damdamdeo.todo.api;

import java.util.Objects;

public class TodoIdAlreadyAffectedException extends RuntimeException {

    private final String todoIdAlreadyAffected;

    public TodoIdAlreadyAffectedException(final String todoIdAlreadyAffected) {
        this.todoIdAlreadyAffected = Objects.requireNonNull(todoIdAlreadyAffected);
    }

    public String todoIdAlreadyAffected() {
        return todoIdAlreadyAffected;
    }

}
