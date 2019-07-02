package com.wineforex.order.domain;

import com.damdamdeo.eventsourcing.domain.AggregateRoot;
import com.wineforex.order.domain.event.CreateOrderEventPayload;

public class OrderAggregate extends AggregateRoot {

    private String articleName;

    private Long quantity;

    public void on(final CreateOrderEventPayload createOrderEventPayload) {
        this.aggregateRootId = createOrderEventPayload.orderId();
        this.articleName = createOrderEventPayload.articleName();
        this.quantity = createOrderEventPayload.quantity();
    }

    @Override
    public String toString() {
        return "OrderAggregate{" +
                "articleName='" + articleName + '\'' +
                ", quantity=" + quantity +
                ", aggregateRootId='" + aggregateRootId + '\'' +
                '}';
    }

    public String articleName() {
        return articleName;
    }

    public Long quantity() {
        return quantity;
    }

}
