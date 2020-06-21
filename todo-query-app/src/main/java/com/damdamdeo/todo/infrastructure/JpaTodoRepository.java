package com.damdamdeo.todo.infrastructure;

import com.damdamdeo.todo.domain.api.Todo;
import com.damdamdeo.todo.domain.TodoRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class JpaTodoRepository implements TodoRepository {

    final EntityManager entityManager;

    public JpaTodoRepository(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public Todo get(final String todoId) {
        return entityManager.find(TodoEntity.class, todoId);
    }

    @Override
    @Transactional
    public List<Todo> fetchAll() {
        return entityManager
                .createQuery("SELECT t FROM TodoEntity t", TodoEntity.class)
                .getResultList()
                .stream()
                .map(Todo.class::cast)
                .collect(Collectors.toList());
    }

    public void persist(final TodoEntity todoEntity) {
        entityManager.persist(todoEntity);
    }

    public void merge(final TodoEntity todoEntity) {
        entityManager.merge(todoEntity);
    }

    public TodoEntity find(final String todoId) {
        return entityManager.find(TodoEntity.class, todoId);
    }

}
