package com.damdamdeo.order.domain;

import com.damdamdeo.eventsourcing.domain.AbstractAggregateRootRepository;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderAggregateRootRepository extends AbstractAggregateRootRepository<OrderAggregate> {

    @Override
    protected OrderAggregate createNewInstance() {
        return new OrderAggregate();
    }

    // TODO method pour checker les unicités !

}
