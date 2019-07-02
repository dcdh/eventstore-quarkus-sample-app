package com.wineforex.order.domain.event;

import com.damdamdeo.eventsourcing.domain.Payload;
import com.wineforex.order.domain.OrderAggregate;

import java.util.Objects;

public class CreateOrderEventPayload extends Payload<OrderAggregate> {

    private final String orderId;

    private final String articleName;

    private final Long quantity;

    public CreateOrderEventPayload(final String orderId,
                                   final String articleName,
                                   final Long quantity) {
        this.orderId = Objects.requireNonNull(orderId);
        this.articleName = Objects.requireNonNull(articleName);
        this.quantity = Objects.requireNonNull(quantity);
    }

    @Override
    protected void apply(final OrderAggregate aggregateRoot) {
        aggregateRoot.on(this);
    }

    public String orderId() {
        return orderId;
    }

    public String articleName() {
        return articleName;
    }

    public Long quantity() {
        return quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateOrderEventPayload that = (CreateOrderEventPayload) o;
        return Objects.equals(orderId, that.orderId) &&
                Objects.equals(articleName, that.articleName) &&
                Objects.equals(quantity, that.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, articleName, quantity);
    }
}
