package com.damdamdeo.order.domain;

import com.damdamdeo.order.api.Order;
import com.damdamdeo.order.api.OrderAggregateRootRepository;

public interface OrderCommand {

    String orderId();

    boolean exactlyOnceCommandExecution();

    Order handle(OrderAggregateRootRepository orderAggregateRootRepository);

}
