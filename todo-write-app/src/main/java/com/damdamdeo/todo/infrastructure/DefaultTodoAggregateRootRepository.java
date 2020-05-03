package com.damdamdeo.todo.infrastructure;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AbstractAggregateRootRepository;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventRepository;
import com.damdamdeo.todo.domain.TodoAggregateRoot;
import com.damdamdeo.todo.domain.TodoAggregateRootRepository;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.math.BigInteger;

@Dependent
public class DefaultTodoAggregateRootRepository extends AbstractAggregateRootRepository<TodoAggregateRoot> implements TodoAggregateRootRepository {

    final EntityManager entityManager;
    final EventRepository eventRepository;
    final AggregateRootSerializer aggregateRootSerializer;

    public DefaultTodoAggregateRootRepository(final EntityManager entityManager,
                                              final EventRepository eventRepository,
                                              final AggregateRootSerializer aggregateRootSerializer) {
        this.entityManager = entityManager;
        this.eventRepository = eventRepository;
        this.aggregateRootSerializer = aggregateRootSerializer;
    }

    @Override
    @Transactional
    public boolean isTodoExistent(final String todoIdToCheck) {
        // je pourrais vérifier en utilisant la colonne aggregaterootId, cependant mon but est de réaliser l'implementation
        // via le contenu json de l'aggregat...

        final BigInteger countAggregateRootId = (BigInteger) entityManager.createNativeQuery("SELECT count(*) as count FROM aggregateroot WHERE aggregateroottype = 'TodoAggregateRoot' AND aggregateroot->>'aggregateRootId' = :aggregateRootId")
                .setParameter("aggregateRootId", todoIdToCheck)
                .getSingleResult();
        return countAggregateRootId.compareTo(BigInteger.ZERO) > 0;
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
    protected EntityManager entityManager() {
        return entityManager;
    }

    @Override
    protected AggregateRootSerializer aggregateRootSerializer() {
        return aggregateRootSerializer;
    }

}
