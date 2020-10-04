package com.damdamdeo.todo.domain;

public final class DomainMarkTodoAsCompletedService implements MarkTodoAsCompletedService {

    private final TodoDomainRepository todoDomainRepository;

    public DomainMarkTodoAsCompletedService(final TodoDomainRepository todoDomainRepository) {
        this.todoDomainRepository = todoDomainRepository;
    }

    @Override
    public TodoDomain markTodoAsCompleted(final String todoId, final Long version) {
        final TodoDomain todoDomain = todoDomainRepository.get(todoId);
        return todoDomainRepository.merge(todoDomain.markAsCompleted(version));
    }

}
