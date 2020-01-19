package com.damdamdeo.todo.infrastructure;

import com.damdamdeo.todo.domain.Todo;
import com.damdamdeo.todo.domain.TodoRepository;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@Dependent
public class JpaTodoRepository implements TodoRepository {

    final EntityManager em;

    public JpaTodoRepository(final EntityManager em) {
        this.em = em;
    }

    @Override
    @Transactional
    public Todo merge(final Todo todo) {
        return em.merge(new TodoEntity(todo));
    }

    @Override
    @Transactional
    public Todo get(final String todoId) {
        return em.find(TodoEntity.class, todoId);
    }

}
