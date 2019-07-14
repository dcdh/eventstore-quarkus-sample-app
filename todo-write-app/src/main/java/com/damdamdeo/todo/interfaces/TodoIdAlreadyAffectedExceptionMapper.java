package com.damdamdeo.todo.interfaces;

import com.damdamdeo.todo.api.TodoIdAlreadyAffectedException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class TodoIdAlreadyAffectedExceptionMapper implements ExceptionMapper<TodoIdAlreadyAffectedException> {

    @Override
    public Response toResponse(final TodoIdAlreadyAffectedException exception) {
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.TEXT_PLAIN)
                .entity(String.format("Le todoId '%s' est déjà affecté.", exception.todoIdAlreadyAffected())).build();
    }

}
