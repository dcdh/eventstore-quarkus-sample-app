package com.damdamdeo.todo.publicfrontend.domain.user;

public final class UnexpectedException extends Exception {

    private final int status;

    public UnexpectedException(final int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

}
