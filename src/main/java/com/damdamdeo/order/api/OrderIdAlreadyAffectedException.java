package com.damdamdeo.order.api;

import java.util.Objects;

public class OrderIdAlreadyAffectedException extends RuntimeException {

    private final String orderIdAlreadyAffected;

    public OrderIdAlreadyAffectedException(final String orderIdAlreadyAffected) {
        this.orderIdAlreadyAffected = Objects.requireNonNull(orderIdAlreadyAffected);
    }

    public String orderIdAlreadyAffected() {
        return orderIdAlreadyAffected;
    }

}
