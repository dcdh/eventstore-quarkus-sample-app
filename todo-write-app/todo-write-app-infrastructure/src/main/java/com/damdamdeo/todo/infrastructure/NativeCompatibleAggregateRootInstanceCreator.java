package com.damdamdeo.todo.infrastructure;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.AggregateRootInstanceCreator;
import com.damdamdeo.todo.domain.TodoAggregateRoot;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

@ApplicationScoped
@Alternative
@Priority(1)
public class NativeCompatibleAggregateRootInstanceCreator implements AggregateRootInstanceCreator {

    @Override
    public <T extends AggregateRoot> T createNewInstance(final Class<T> clazz,
                                                         final String aggregateRootId) {
        return (T) new TodoAggregateRoot(aggregateRootId);
    }

}
