package com.damdamdeo.todo.interfaces;

import com.damdamdeo.todo.command.CreateNewTodoCommand;
import com.damdamdeo.todo.command.MarkTodoAsCompletedCommand;
import com.damdamdeo.todo.command.handler.CreateNewTodoCommandHandler;
import com.damdamdeo.todo.command.handler.MarkTodoAsCompletedCommandHandler;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Objects;

@Path("/todos")
@Produces(MediaType.APPLICATION_JSON)
public class TodoEndpoint {

    final CreateNewTodoCommandHandler createNewTodoCommandHandler;

    final MarkTodoAsCompletedCommandHandler markTodoAsCompletedCommandHandler;

    public TodoEndpoint(final CreateNewTodoCommandHandler createNewTodoCommandHandler,
                        final MarkTodoAsCompletedCommandHandler markTodoAsCompletedCommandHandler) {
        this.createNewTodoCommandHandler = Objects.requireNonNull(createNewTodoCommandHandler);
        this.markTodoAsCompletedCommandHandler = Objects.requireNonNull(markTodoAsCompletedCommandHandler);
    }

    @POST
    @Path("/createNewTodo")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public TodoDTO createNewTodo(@FormParam("todoId") final String todoId,
                                 @FormParam("description") final String description) throws Throwable {
        return new TodoDTO(createNewTodoCommandHandler.executeCommand(new CreateNewTodoCommand(todoId, description)));
    }

    @POST
    @Path("/markTodoAsCompleted")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public TodoDTO markTodoAsCompletedCommand(@FormParam("todoId") final String todoId) throws Throwable {
        return new TodoDTO(markTodoAsCompletedCommandHandler.executeCommand(new MarkTodoAsCompletedCommand(todoId)));
    }

}
