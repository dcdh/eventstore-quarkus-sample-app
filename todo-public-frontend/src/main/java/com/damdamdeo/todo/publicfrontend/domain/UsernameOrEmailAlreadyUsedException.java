package com.damdamdeo.todo.publicfrontend.domain;

import java.util.Objects;

public final class UsernameOrEmailAlreadyUsedException extends Exception {

    private final String username;
    private final String email;

    public UsernameOrEmailAlreadyUsedException(final String username, final String email) {
        this.username = Objects.requireNonNull(username);
        this.email = Objects.requireNonNull(email);
    }

    public String username() {
        return username;
    }

    public String email() {
        return email;
    }

}
