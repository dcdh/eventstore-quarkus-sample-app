package com.damdamdeo.todo.interfaces;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.UnknownAggregateRootException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UnknownAggregateRootExceptionMapper implements ExceptionMapper<UnknownAggregateRootException> {

    @Override
    public Response toResponse(final UnknownAggregateRootException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.TEXT_PLAIN)
                .entity(String.format("Le todoId '%s' est inconnu.", exception.unknownAggregateId())).build();
    }

}
