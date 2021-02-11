package com.damdamdeo.todo.domain.api.usecase;

public interface UseCase<C extends UseCaseCommand, O> {

    O execute(C command) throws UseCaseException;

}
