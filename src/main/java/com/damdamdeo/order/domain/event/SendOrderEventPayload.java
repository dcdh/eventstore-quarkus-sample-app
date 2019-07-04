package com.damdamdeo.order.domain.event;

import com.damdamdeo.eventsourcing.domain.Payload;
import com.damdamdeo.order.domain.OrderAggregate;

import java.util.Objects;

public class SendOrderEventPayload extends Payload<OrderAggregate> {

    private final String orderId;

    public SendOrderEventPayload(final String orderId) {
        this.orderId = Objects.requireNonNull(orderId);
    }

    public String orderId() {
        return orderId;
    }

    @Override
    protected void apply(final OrderAggregate aggregateRoot) {
        aggregateRoot.on(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SendOrderEventPayload that = (SendOrderEventPayload) o;
        return Objects.equals(orderId, that.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }
}
