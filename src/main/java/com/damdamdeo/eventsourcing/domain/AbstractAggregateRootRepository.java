package com.damdamdeo.eventsourcing.domain;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;

// TODO les implementation correspondrons aux Implementations des aggregats !!!
// TODO faire des tests !!! avec une vrai base de données !!!!
public abstract class AbstractAggregateRootRepository<T extends AggregateRoot> implements AggregateRootRepository<T> {

    @Inject
    EventRepository eventRepository;

    @Override
    public T save(final T aggregateRoot) {
        Objects.requireNonNull(aggregateRoot);
        eventRepository.save(aggregateRoot.unsavedEvents());
        aggregateRoot.deleteUnsavedEvents();
        return aggregateRoot;
    }

    @Override
    public T load(final String aggregateRootId) throws UnknownAggregateRootException {
        Objects.requireNonNull(aggregateRootId);
        final T instance = createNewInstance();
        final List<Event> events = eventRepository.load(aggregateRootId, instance.getClass().getName());
        if (events.size() == 0) {
            throw new UnknownAggregateRootException(aggregateRootId);
        }
        instance.loadFromHistory(events);
        return instance;
    }

    protected abstract T createNewInstance();

}
