package com.damdamdeo.todo.infrastructure;

import com.damdamdeo.todo.aggregate.Todo;
import com.damdamdeo.todo.aggregate.TodoRepository;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@Dependent
public class JpaTodoRepository implements TodoRepository {

    final EntityManager entityManager;

    public JpaTodoRepository(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public Todo merge(final Todo todo) {
        return entityManager.merge(new TodoEntity(todo));
    }

    @Override
    @Transactional
    public Todo get(final String todoId) {
        return entityManager.find(TodoEntity.class, todoId);
    }

}
