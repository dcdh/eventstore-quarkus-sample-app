package com.damdamdeo.todo.interfaces;

import com.damdamdeo.todo.domain.api.UnknownTodoException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UnknownTodoExceptionMapper implements ExceptionMapper<UnknownTodoException> {

    @Override
    public Response toResponse(final UnknownTodoException exception) {
        return Response.status(Response.Status.NOT_FOUND)
                .type(MediaType.TEXT_PLAIN)
                .entity(String.format("Le todoId '%s' est inconnu.", exception.unknownTodoId())).build();
    }

}
