package com.damdamdeo.eventsourcing.domain;

import java.util.List;
import java.util.Objects;

public abstract class AbstractAggregateRootRepository<T extends AggregateRoot> implements AggregateRootRepository<T> {

    @Override
    public T save(final T aggregateRoot) {
        Objects.requireNonNull(aggregateRoot);
        final EventRepository eventRepository = eventRepository();
        final AggregateRootProjectionRepository aggregateRootProjectionRepository = aggregateRootProjectionRepository();
        eventRepository.save(aggregateRoot.unsavedEvents());
        aggregateRoot.deleteUnsavedEvents();
        aggregateRootProjectionRepository.save(new AggregateRootProjection(aggregateRoot));
        return aggregateRoot;
    }

    @Override
    public T load(final String aggregateRootId) throws UnknownAggregateRootException {
        Objects.requireNonNull(aggregateRootId);
        final EventRepository eventRepository = eventRepository();
        final T instance = createNewInstance();
        final List<Event> events = eventRepository.load(aggregateRootId, instance.getClass().getSimpleName());
        if (events.size() == 0) {
            throw new UnknownAggregateRootException(aggregateRootId);
        }
        instance.loadFromHistory(events);
        return instance;
    }

    protected abstract T createNewInstance();

    protected abstract EventRepository eventRepository();

    protected abstract AggregateRootProjectionRepository aggregateRootProjectionRepository();

}
