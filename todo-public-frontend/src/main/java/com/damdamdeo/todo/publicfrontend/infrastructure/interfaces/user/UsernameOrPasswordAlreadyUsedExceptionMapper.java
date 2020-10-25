package com.damdamdeo.todo.publicfrontend.infrastructure.interfaces.user;

import com.damdamdeo.todo.publicfrontend.domain.user.UsernameOrEmailAlreadyUsedException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UsernameOrPasswordAlreadyUsedExceptionMapper implements ExceptionMapper<UsernameOrEmailAlreadyUsedException> {

    @Override
    public Response toResponse(final UsernameOrEmailAlreadyUsedException exception) {
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.TEXT_PLAIN)
                .entity(String.format("Username '%s' or email '%s' already used", exception.username(), exception.email())).build();
    }

}
