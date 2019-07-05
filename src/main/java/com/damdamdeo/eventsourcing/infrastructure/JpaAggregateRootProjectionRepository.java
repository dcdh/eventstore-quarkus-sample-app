package com.damdamdeo.eventsourcing.infrastructure;

import com.damdamdeo.eventsourcing.domain.AggregateRootProjection;
import com.damdamdeo.eventsourcing.domain.AggregateRootProjectionRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Optional;

@ApplicationScoped
public class JpaAggregateRootProjectionRepository implements AggregateRootProjectionRepository {

    @Inject
    EntityManager em;

    @Override
    @Transactional
    public AggregateRootProjection save(final AggregateRootProjection aggregateRootProjection) {
        final AggregateRootProjectionEntity aggregateRootProjectionEntity = new AggregateRootProjectionEntity(aggregateRootProjection);
        return Optional.of(em.merge(aggregateRootProjectionEntity))
                .map(AggregateRootProjectionEntity::toAggregateRootProjection)
                .get();
    }

}
