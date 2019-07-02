package com.wineforex.order.domain;

import com.damdamdeo.eventsourcing.domain.AbstractAggregateRootRepository;
import com.wineforex.order.domain.OrderAggregate;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class OrderAggregateRootRepository extends AbstractAggregateRootRepository<OrderAggregate> {

    @Override
    protected OrderAggregate createNewInstance() {
        return new OrderAggregate();
    }

}
