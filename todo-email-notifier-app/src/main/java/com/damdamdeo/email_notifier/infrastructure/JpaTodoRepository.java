package com.damdamdeo.email_notifier.infrastructure;

import com.damdamdeo.email_notifier.domain.Todo;
import com.damdamdeo.email_notifier.domain.TodoRepository;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;

@Dependent
public class JpaTodoRepository implements TodoRepository {

    final EntityManager em;

    public JpaTodoRepository(final EntityManager em) {
        this.em = em;
    }

    @Override
    public Todo merge(final Todo todo) {
        em.clear();
        return em.merge(new TodoEntity(todo));
    }

    @Override
    public Todo get(final String todoId) {
        em.clear();
        return em.find(TodoEntity.class, todoId);
    }

}
