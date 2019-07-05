package com.damdamdeo.order.api;

import com.damdamdeo.eventsourcing.domain.AggregateRootRepository;
import com.damdamdeo.order.domain.OrderAggregateRoot;

public interface OrderAggregateRootRepository extends AggregateRootRepository<OrderAggregateRoot> {

    boolean isOrderIdAffected(String orderIdToCheck);

}
