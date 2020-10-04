package com.damdamdeo.todo.infrastructure;

import com.damdamdeo.todo.domain.TodoDomain;
import com.damdamdeo.todo.domain.TodoDomainRepository;
import com.damdamdeo.todo.domain.api.UnknownTodoException;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
public class JpaTodoDomainRepository implements TodoDomainRepository {

    private final EntityManager entityManager;

    public JpaTodoDomainRepository(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public TodoDomain get(String todoId) throws UnknownTodoException {
        return Optional.ofNullable(entityManager.find(TodoEntity.class, todoId))
                .map(TodoEntity::toDomain)
                .orElseThrow(() -> new UnknownTodoException(todoId));
    }

    @Override
    @Transactional
    public TodoDomain persist(TodoDomain todoDomain) {
        final TodoEntity todoEntity = TodoEntity.newBuilder()
                .withTodoId(todoDomain.todoId())
                .withDescription(todoDomain.description())
                .withTodoStatus(todoDomain.todoStatus())
                .withVersion(todoDomain.version())
                .build();
        entityManager.persist(todoEntity);
        return todoDomain;
    }

    @Override
    @Transactional
    public TodoDomain merge(TodoDomain todoDomain) {
        final TodoEntity todoEntity = TodoEntity.newBuilder()
                .withTodoId(todoDomain.todoId())
                .withDescription(todoDomain.description())
                .withTodoStatus(todoDomain.todoStatus())
                .withVersion(todoDomain.version())
                .build();
        return entityManager.merge(todoEntity).toDomain();
    }

    @Override
    @Transactional
    public List<TodoDomain> fetchAll() {
        return entityManager
                .createQuery("SELECT t FROM TodoEntity t", TodoEntity.class)
                .getResultList()
                .stream()
                .map(TodoEntity::toDomain)
                .collect(Collectors.toList());
    }

}
