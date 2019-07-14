package com.damdamdeo.email_notifier.infrastructure;

import com.damdamdeo.email_notifier.domain.Todo;
import com.damdamdeo.email_notifier.domain.TodoRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;

@ApplicationScoped
public class JpaTodoRepository implements TodoRepository {

    final EntityManager em;

    public JpaTodoRepository(final EntityManager em) {
        this.em = em;
    }

    @Override
    public Todo merge(final Todo todo) {
        return em.merge(new TodoEntity(todo));
    }

    @Override
    public Todo get(final String todoId) {
        return em.find(TodoEntity.class, todoId);
    }

}
