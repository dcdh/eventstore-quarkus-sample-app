package com.damdamdeo.order.api;

public interface Order {

    String orderId();

    String articleName();

    Long quantity();

    Boolean send();

    Long version();

}
