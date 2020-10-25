package com.damdamdeo.todo.publicfrontend.infrastructure.interfaces.authentication;

import com.damdamdeo.todo.publicfrontend.domain.authentication.RefreshTokenInvalidException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RefreshTokenInvalidExceptionMapper implements ExceptionMapper<RefreshTokenInvalidException> {

    @Override
    public Response toResponse(final RefreshTokenInvalidException exception) {
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.TEXT_PLAIN)
                .entity(String.format("Refresh token invalid")).build();
    }

}