package com.damdamdeo.eventsourcing.infrastructure;

import com.damdamdeo.eventsourcing.domain.AggregateRootProjection;
import com.damdamdeo.eventsourcing.domain.AggregateRootProjectionRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class JpaAggregateRootProjectionRepository implements AggregateRootProjectionRepository {

    final EntityManager em;

    public JpaAggregateRootProjectionRepository(final EntityManager em) {
        this.em = Objects.requireNonNull(em);
    }

    @Override
    @Transactional
    public AggregateRootProjection save(final AggregateRootProjection aggregateRootProjection) {
        final AggregateRootProjectionEntity aggregateRootProjectionEntity = new AggregateRootProjectionEntity(aggregateRootProjection);
        return Optional.of(em.merge(aggregateRootProjectionEntity))
                .map(AggregateRootProjectionEntity::toAggregateRootProjection)
                .get();
    }

}
