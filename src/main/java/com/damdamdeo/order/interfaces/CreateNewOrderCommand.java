package com.damdamdeo.order.interfaces;

import com.damdamdeo.order.api.Order;
import com.damdamdeo.order.domain.OrderAggregate;
import com.damdamdeo.order.domain.OrderAggregateRootRepository;
import com.damdamdeo.order.domain.OrderCommand;
import com.damdamdeo.order.domain.event.CreateOrderEventPayload;

import java.util.Objects;

public class CreateNewOrderCommand implements OrderCommand {

    private String orderId;

    private String articleName;

    private Long quantity;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getArticleName() {
        return articleName;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateNewOrderCommand that = (CreateNewOrderCommand) o;
        return Objects.equals(orderId, that.orderId) &&
                Objects.equals(articleName, that.articleName) &&
                Objects.equals(quantity, that.quantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, articleName, quantity);
    }

    @Override
    public String orderId() {
        return null;
    }

    @Override
    public boolean exactlyOnceCommandExecution() {
        return true;
    }

    @Override
    public Order handle(final OrderAggregateRootRepository orderAggregateRootRepository) {
        // TODO check if name not already used :)
        final OrderAggregate orderAggregate = new OrderAggregate();
        orderAggregate.apply(new CreateOrderEventPayload(orderId,
                articleName,
                quantity));
        return orderAggregateRootRepository.save(orderAggregate);
    }

}
