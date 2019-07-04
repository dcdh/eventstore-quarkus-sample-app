package com.damdamdeo.order.domain;

import com.damdamdeo.eventsourcing.domain.AggregateRoot;
import com.damdamdeo.order.api.Order;
import com.damdamdeo.order.domain.event.CreateOrderEventPayload;
import com.damdamdeo.order.domain.event.SendOrderEventPayload;

public class OrderAggregate extends AggregateRoot implements Order {

    private String articleName;

    private Long quantity;

    private Boolean send;

    public void on(final CreateOrderEventPayload createOrderEventPayload) {
        this.aggregateRootId = createOrderEventPayload.orderId();
        this.articleName = createOrderEventPayload.articleName();
        this.quantity = createOrderEventPayload.quantity();
        this.send = Boolean.FALSE;
    }

    public void on(final SendOrderEventPayload sendOrderEventPayload) {
        this.send = Boolean.TRUE;
    }

    @Override
    public String toString() {
        return "OrderAggregate{" +
                "articleName='" + articleName + '\'' +
                ", quantity=" + quantity +
                ", send=" + send +
                '}';
    }

    @Override
    public String orderId() {
        return aggregateRootId;
    }

    @Override
    public String articleName() {
        return articleName;
    }

    @Override
    public Long quantity() {
        return quantity;
    }

    @Override
    public Boolean send() {
        return send;
    }

}
