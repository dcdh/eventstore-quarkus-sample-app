package com.damdamdeo.todo.domain.usecase;

import com.damdamdeo.todo.domain.TodoDomain;
import com.damdamdeo.todo.domain.TodoDomainRepository;
import com.damdamdeo.todo.domain.api.TodoStatus;
import com.damdamdeo.todo.domain.api.usecase.UseCase;
import com.damdamdeo.todo.domain.api.usecase.UseCaseException;

public class CreateTodoUseCase implements UseCase<CreateTodoCommand, TodoDomain> {

    private final TodoDomainRepository todoDomainRepository;

    public CreateTodoUseCase(final TodoDomainRepository todoDomainRepository) {
        this.todoDomainRepository = todoDomainRepository;
    }

    @Override
    public TodoDomain execute(final CreateTodoCommand command) throws UseCaseException {
        final TodoDomain todoDomain = TodoDomain.newBuilder()
                .withTodoId(command.todoId())
                .withDescription(command.description())
                .withTodoStatus(TodoStatus.IN_PROGRESS)
                .withVersion(command.version())
                .build();
        return todoDomainRepository.persist(todoDomain);
    }

}
