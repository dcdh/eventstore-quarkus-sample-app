package com.damdamdeo.todo.interfaces;

import com.damdamdeo.todo.domain.api.TodoAlreadyMarkedAsCompletedException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class TodoAlreadyMarkedAsCompletedExceptionMapper implements ExceptionMapper<TodoAlreadyMarkedAsCompletedException> {

    @Override
    public Response toResponse(final TodoAlreadyMarkedAsCompletedException exception) {
        return Response.status(Response.Status.CONFLICT)
                .type(MediaType.TEXT_PLAIN)
                .entity(String.format("Le todo '%s' est déjà complété.", exception.todoAlreadyMarkedAsCompleted().todoId())).build();
    }

}
