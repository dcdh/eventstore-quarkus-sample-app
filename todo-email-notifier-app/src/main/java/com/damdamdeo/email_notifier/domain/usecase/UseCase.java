package com.damdamdeo.email_notifier.domain.usecase;

public interface UseCase<C extends UseCaseCommand, O> {

    O execute(C command) throws UseCaseException;

}
