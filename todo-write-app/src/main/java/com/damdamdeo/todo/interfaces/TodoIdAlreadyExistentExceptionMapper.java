package com.damdamdeo.todo.interfaces;

import com.damdamdeo.todo.domain.api.TodoAlreadyExistentException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class TodoIdAlreadyExistentExceptionMapper implements ExceptionMapper<TodoAlreadyExistentException> {

    @Override
    public Response toResponse(final TodoAlreadyExistentException exception) {
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.TEXT_PLAIN)
                .entity(String.format("Le todoId '%s' est déjà existant.", exception.todoIdAlreadyExistent().todoId())).build();
    }

}
