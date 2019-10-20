package com.damdamdeo.todo.infrastructure;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AbstractAggregateRootRepository;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootProjectionRepository;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventRepository;
import com.damdamdeo.todo.aggregate.TodoAggregateRoot;
import com.damdamdeo.todo.aggregate.TodoAggregateRootRepository;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.math.BigInteger;

@Dependent
public class DefaultTodoAggregateRootRepository extends AbstractAggregateRootRepository<TodoAggregateRoot> implements TodoAggregateRootRepository {

    final EntityManager em;
    final EventRepository eventRepository;
    final AggregateRootProjectionRepository aggregateRootProjectionRepository;

    public DefaultTodoAggregateRootRepository(final EntityManager em,
                                              final EventRepository eventRepository,
                                              final AggregateRootProjectionRepository aggregateRootProjectionRepository) {
        this.em = em;
        this.eventRepository = eventRepository;
        this.aggregateRootProjectionRepository = aggregateRootProjectionRepository;
    }

    @Override
    protected TodoAggregateRoot createNewInstance() {
        return new TodoAggregateRoot();
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
    public boolean isTodoExistent(final String todoIdToCheck) {
        // je pourrais vérifier en utilisant la colonne aggregaterootId, cependant mon but est de réaliser l'implementation
        // via le contenu json de l'aggregat...

        final BigInteger countAggregateRootId = (BigInteger) em.createNativeQuery("SELECT count(*) as count FROM aggregaterootprojection WHERE aggregateroottype = 'TodoAggregateRoot' AND aggregateroot->>'aggregateRootId' = :aggregateRootId")
                .setParameter("aggregateRootId", todoIdToCheck)
                .getSingleResult();
        return countAggregateRootId.compareTo(BigInteger.ZERO) > 0;
    }

}
