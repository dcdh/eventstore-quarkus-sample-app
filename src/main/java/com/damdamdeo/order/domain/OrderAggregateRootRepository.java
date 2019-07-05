package com.damdamdeo.order.domain;

import com.damdamdeo.eventsourcing.domain.AbstractAggregateRootRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.math.BigInteger;

@ApplicationScoped
public class OrderAggregateRootRepository extends AbstractAggregateRootRepository<OrderAggregateRoot> {

    @Inject
    EntityManager em;

    @Override
    protected OrderAggregateRoot createNewInstance() {
        return new OrderAggregateRoot();
    }

    @Transactional
    public boolean isOrderIdAffected(final String orderIdToCheck) {
        // je pourrais vérifier en utilisant la colonne aggregaterootId, cependant mon but est de réaliser l'implementation
        // via le contenu json de l'aggregat...

        final BigInteger countAggregateRootId = (BigInteger) em.createNativeQuery("SELECT count(*) as count FROM aggregaterootprojection WHERE aggregateroottype = 'OrderAggregateRoot' AND aggregateroot->>'aggregateRootId' = :aggregateRootId")
                .setParameter("aggregateRootId", "orderId")
                .getSingleResult();
        return countAggregateRootId.compareTo(BigInteger.ZERO) > 0;
    }

}
