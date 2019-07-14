package com.damdamdeo.email_notifier.infrastructure;

import com.damdamdeo.email_notifier.domain.EventStoreRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@ApplicationScoped
public class JpaEventStoreRepository implements EventStoreRepository {

    final EntityManager em;

    public JpaEventStoreRepository(final EntityManager em) {
        this.em = Objects.requireNonNull(em);
    }

    @Override
    @Transactional
    public void markEventAsConsumed(final UUID eventId, final Date consumedAt) {
        em.persist(new KafkaEventEntity(eventId, consumedAt));
    }

    @Override
    @Transactional
    public boolean hasConsumedEvent(final UUID eventId) {
        return em.createQuery("SELECT CASE WHEN (COUNT(e) > 0) THEN TRUE ELSE FALSE END " +
                "FROM KafkaEventEntity e WHERE e.eventId = :eventId", Boolean.class)
                .setParameter("eventId", eventId)
                .getSingleResult();
    }

}
