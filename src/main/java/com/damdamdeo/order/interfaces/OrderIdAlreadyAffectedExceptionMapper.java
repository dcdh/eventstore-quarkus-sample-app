package com.damdamdeo.order.interfaces;

import com.damdamdeo.order.api.OrderIdAlreadyAffectedException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class OrderIdAlreadyAffectedExceptionMapper implements ExceptionMapper<OrderIdAlreadyAffectedException> {

    @Override
    public Response toResponse(final OrderIdAlreadyAffectedException exception) {
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.TEXT_PLAIN)
                .entity(String.format("L'orderId '%s' est déjà affecté.", exception.orderIdAlreadyAffected())).build();
    }

}
