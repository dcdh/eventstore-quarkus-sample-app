package com.damdamdeo.order.domain;

import com.damdamdeo.eventsourcing.domain.AbstractAggregateRootRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderAggregateRootRepository extends AbstractAggregateRootRepository<OrderAggregateRoot> {

    @Override
    protected OrderAggregateRoot createNewInstance() {
        return new OrderAggregateRoot();
    }

    // TODO method pour checker les unicités !

}
