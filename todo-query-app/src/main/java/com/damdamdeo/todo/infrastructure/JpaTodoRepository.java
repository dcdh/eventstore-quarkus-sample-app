package com.damdamdeo.todo.infrastructure;

import com.damdamdeo.todo.domain.Todo;
import com.damdamdeo.todo.domain.TodoRepository;

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
        em.clear();// Cela me choque. Mais comme je multiplie les instances d'entityManager
        // je suis obligé de clear pour faire en sorte de récupérer la derniére version en base et non celle en cache
        // de cet entityManager
        // TODO trouver une facon pour desactiver le cache pour tous !
        return em.find(TodoEntity.class, todoId);
    }

}
