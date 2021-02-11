package com.damdamdeo.email_notifier.domain.usecase;

public class UseCaseException extends RuntimeException {

    public UseCaseException(final Throwable cause) {
        super(cause);
    }

    public UseCaseException() {
        super();
    }
}
