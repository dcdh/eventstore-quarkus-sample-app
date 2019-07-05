package com.damdamdeo.order.interfaces;

import com.damdamdeo.order.api.Order;
import com.damdamdeo.order.api.OrderAggregateRootRepository;
import com.damdamdeo.order.domain.OrderAggregateRoot;
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
        final OrderAggregateRoot orderAggregateRoot = orderAggregateRootRepository.load(orderId);
        orderAggregateRoot.apply(new SendOrderEventPayload(orderId));
        return orderAggregateRootRepository.save(orderAggregateRoot);
    }

}
