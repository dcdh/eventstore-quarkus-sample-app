package com.damdamdeo.todo.domain.usecase;

import com.damdamdeo.todo.domain.TodoDomain;
import com.damdamdeo.todo.domain.TodoDomainRepository;
import com.damdamdeo.todo.domain.api.usecase.UseCase;
import com.damdamdeo.todo.domain.api.usecase.UseCaseException;

public class MarkTodoAsCompletedUseCase implements UseCase<MarkTodoAsCompletedCommand, TodoDomain> {

    private final TodoDomainRepository todoDomainRepository;

    public MarkTodoAsCompletedUseCase(final TodoDomainRepository todoDomainRepository) {
        this.todoDomainRepository = todoDomainRepository;
    }

    @Override
    public TodoDomain execute(final MarkTodoAsCompletedCommand command) throws UseCaseException {
        final TodoDomain todoDomain = todoDomainRepository.get(command.todoId());
        return todoDomainRepository.merge(todoDomain.markAsCompleted(command.version()));
    }

}
