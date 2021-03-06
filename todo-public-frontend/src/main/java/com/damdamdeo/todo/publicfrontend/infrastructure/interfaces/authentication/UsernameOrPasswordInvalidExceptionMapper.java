package com.damdamdeo.todo.publicfrontend.infrastructure.interfaces.authentication;

import com.damdamdeo.todo.publicfrontend.domain.authentication.UsernameOrPasswordInvalidException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UsernameOrPasswordInvalidExceptionMapper implements ExceptionMapper<UsernameOrPasswordInvalidException> {

    @Override
    public Response toResponse(final UsernameOrPasswordInvalidException exception) {
        return Response.status(Response.Status.UNAUTHORIZED)
                .type(MediaType.TEXT_PLAIN)
                .entity(String.format("Username or password invalid")).build();
    }

}
