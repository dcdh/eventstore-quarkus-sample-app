package com.damdamdeo.order.interfaces;

import com.damdamdeo.order.api.Order;
import com.damdamdeo.order.domain.OrderAggregate;
import com.damdamdeo.order.domain.OrderAggregateRootRepository;
import com.damdamdeo.order.domain.OrderCommand;
import com.damdamdeo.order.domain.event.SendOrderEventPayload;

public class SendOrderCommand implements OrderCommand {

    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public String orderId() {
        return orderId;
    }

    @Override
    public boolean exactlyOnceCommandExecution() {
        return false;
    }

    @Override
    public Order handle(final OrderAggregateRootRepository orderAggregateRootRepository) {
        final OrderAggregate orderAggregate = orderAggregateRootRepository.load(orderId);
        orderAggregate.apply(new SendOrderEventPayload(orderId));
        return orderAggregateRootRepository.save(orderAggregate);
    }

}
