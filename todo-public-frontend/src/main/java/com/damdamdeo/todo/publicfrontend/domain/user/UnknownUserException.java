package com.damdamdeo.todo.publicfrontend.domain.user;

import java.util.Objects;

public final class UnknownUserException extends Exception {

    private final String email;

    public UnknownUserException(final String email) {
        this.email = Objects.requireNonNull(email);
    }

    public String email() {
        return email;
    }

}
