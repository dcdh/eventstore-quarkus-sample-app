package com.damdamdeo.order.domain;

import com.damdamdeo.eventsourcing.domain.AbstractAggregateRootRepository;
import com.damdamdeo.eventsourcing.domain.AggregateRootProjectionRepository;
import com.damdamdeo.eventsourcing.domain.EventRepository;
import com.damdamdeo.order.api.OrderAggregateRootRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.math.BigInteger;

@ApplicationScoped
public class DefaultOrderAggregateRootRepository extends AbstractAggregateRootRepository<OrderAggregateRoot> implements OrderAggregateRootRepository {

    final EntityManager em;
    final EventRepository eventRepository;
    final AggregateRootProjectionRepository aggregateRootProjectionRepository;

    public DefaultOrderAggregateRootRepository(final EntityManager em,
                                               final EventRepository eventRepository,
                                               final AggregateRootProjectionRepository aggregateRootProjectionRepository) {
        this.em = em;
        this.eventRepository = eventRepository;
        this.aggregateRootProjectionRepository = aggregateRootProjectionRepository;
    }

    @Override
    protected OrderAggregateRoot createNewInstance() {
        return new OrderAggregateRoot();
    }

    @Override
    protected EventRepository eventRepository() {
        return eventRepository;
    }

    @Override
    protected AggregateRootProjectionRepository aggregateRootProjectionRepository() {
        return aggregateRootProjectionRepository;
    }

    @Override
    @Transactional
    public boolean isOrderIdAffected(final String orderIdToCheck) {
        // je pourrais vérifier en utilisant la colonne aggregaterootId, cependant mon but est de réaliser l'implementation
        // via le contenu json de l'aggregat...

        final BigInteger countAggregateRootId = (BigInteger) em.createNativeQuery("SELECT count(*) as count FROM aggregaterootprojection WHERE aggregateroottype = 'OrderAggregateRoot' AND aggregateroot->>'aggregateRootId' = :aggregateRootId")
                .setParameter("aggregateRootId", orderIdToCheck)
                .getSingleResult();
        return countAggregateRootId.compareTo(BigInteger.ZERO) > 0;
    }

}
