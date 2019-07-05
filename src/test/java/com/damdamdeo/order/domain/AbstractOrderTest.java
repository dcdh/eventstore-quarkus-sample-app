package com.damdamdeo.order.domain;

import org.junit.jupiter.api.BeforeEach;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

public abstract class AbstractOrderTest {

    @Inject
    EntityManager em;

    @BeforeEach
    @Transactional
    public void setup() {
        em.createQuery("DELETE FROM EventEntity").executeUpdate();
        em.createQuery("DELETE FROM AggregateRootProjectionEntity").executeUpdate();
    }

}
