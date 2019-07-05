package com.damdamdeo.order.interfaces;

import com.damdamdeo.order.domain.OrderCommandHandler;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Objects;

// https://www.infoq.com/articles/rest-api-on-cqrs/

@Path("/orders")
@Produces(MediaType.APPLICATION_JSON)
public class OrderEndpoint {

    final OrderCommandHandler orderCommandHandler;

    public OrderEndpoint(final OrderCommandHandler orderCommandHandler) {
        this.orderCommandHandler = Objects.requireNonNull(orderCommandHandler);
    }

    @POST
    @Path("/createNewOrder")
    @Consumes(MediaType.APPLICATION_JSON)
    public OrderDTO createNewOrder(final CreateNewOrderCommand createNewOrderCommand) throws Throwable {
        return new OrderDTO(orderCommandHandler.handle(createNewOrderCommand));
    }

    @POST
    @Path("/sendOrder")
    @Consumes(MediaType.APPLICATION_JSON)
    public OrderDTO sendOrderCommand(final SendOrderCommand sendOrderCommand) throws Throwable {
        return new OrderDTO(orderCommandHandler.handle(sendOrderCommand));
    }

}
