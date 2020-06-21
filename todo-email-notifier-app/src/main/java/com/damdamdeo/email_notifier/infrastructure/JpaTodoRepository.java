package com.damdamdeo.email_notifier.infrastructure;

import com.damdamdeo.email_notifier.consumer.TodoRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import java.util.Objects;

@ApplicationScoped
public class JpaTodoRepository implements TodoRepository {

    final EntityManager entityManager;

    public JpaTodoRepository(final EntityManager entityManager) {
        this.entityManager = Objects.requireNonNull(entityManager);
    }

    @Override
    public TodoEntity find(String todoId) {
        return entityManager.find(TodoEntity.class, todoId);
    }

    @Override
    public void persist(TodoEntity todoEntity) {
        entityManager.persist(todoEntity);
    }

    @Override
    public TodoEntity merge(TodoEntity todoEntity) {
        return entityManager.merge(todoEntity);
    }
}
