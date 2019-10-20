package com.damdamdeo.todo;

import org.junit.jupiter.api.BeforeEach;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

public abstract class AbstractTodoTest {

    @Inject
    EntityManager entityManager;

    @BeforeEach
    @Transactional
    public void setup() {
        entityManager.createQuery("DELETE FROM EventEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM AggregateRootProjectionEntity").executeUpdate();
    }

}
