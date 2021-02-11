package com.damdamdeo.todo.domain.api.usecase;

public class UseCaseException extends RuntimeException {

    public UseCaseException(final Throwable cause) {
        super(cause);
    }

    public UseCaseException() {
        super();
    }
}
