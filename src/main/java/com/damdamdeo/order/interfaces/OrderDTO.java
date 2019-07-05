package com.damdamdeo.order.interfaces;

import com.damdamdeo.order.api.Order;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class OrderDTO {

    public String orderId;

    public String articleName;

    public Long quantity;

    public Boolean send;

    public Long version;

    public OrderDTO() {}

    public OrderDTO(final Order order) {
        this.orderId = order.orderId();
        this.articleName = order.articleName();
        this.quantity = order.quantity();
        this.send = order.send();
        this.version = order.version();
    }

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

    public Boolean getSend() {
        return send;
    }

    public void setSend(Boolean send) {
        this.send = send;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
