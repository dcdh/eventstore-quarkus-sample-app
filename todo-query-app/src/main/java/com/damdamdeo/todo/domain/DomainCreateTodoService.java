package com.damdamdeo.todo.domain;

import com.damdamdeo.todo.domain.api.TodoStatus;

public final class DomainCreateTodoService implements CreateTodoService {

    private final TodoDomainRepository todoDomainRepository;

    public DomainCreateTodoService(final TodoDomainRepository todoDomainRepository) {
        this.todoDomainRepository = todoDomainRepository;
    }

    @Override
    public TodoDomain createTodo(final String todoId, final String description, final Long version) {
        final TodoDomain todoDomain = TodoDomain.newBuilder()
                .withTodoId(todoId)
                .withDescription(description)
                .withTodoStatus(TodoStatus.IN_PROGRESS)
                .withVersion(version)
                .build();
        return todoDomainRepository.persist(todoDomain);
    }

}
