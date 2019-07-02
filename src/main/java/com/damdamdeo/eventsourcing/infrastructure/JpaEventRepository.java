package com.damdamdeo.eventsourcing.infrastructure;

import com.damdamdeo.eventsourcing.domain.Event;
import com.damdamdeo.eventsourcing.domain.EventRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class JpaEventRepository implements EventRepository {

    @Inject
    EntityManager em;

    @Transactional
    @Override
    public void save(final List<Event> events) {
        events.stream()
                .map(event -> new EventEntity(event))
                .forEach(eventEntity -> em.persist(eventEntity));
    }

    @Override
    public List<Event> load(final String aggregateRootId, final String aggregateRootType) {
        return em.createNamedQuery("Events.findByAggregateRootIdOrderByVersionAsc", EventEntity.class)
                .setParameter("aggregateRootId", aggregateRootId)
                .setParameter("aggregateRootType", aggregateRootType)
                .getResultStream()
                .map(EventEntity::toEvent)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

}
