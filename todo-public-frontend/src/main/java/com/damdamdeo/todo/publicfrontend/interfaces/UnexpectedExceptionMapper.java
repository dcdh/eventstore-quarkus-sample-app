package com.damdamdeo.todo.publicfrontend.interfaces;

import com.damdamdeo.todo.publicfrontend.domain.user.UnexpectedException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UnexpectedExceptionMapper implements ExceptionMapper<UnexpectedException> {

    @Override
    public Response toResponse(final UnexpectedException exception) {
        return Response.status(exception.getStatus())
                .type(MediaType.TEXT_PLAIN)
                .entity("Unexpected exception").build();
    }

}
