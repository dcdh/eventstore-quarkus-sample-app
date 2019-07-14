package com.damdamdeo.todo.interfaces;

import com.damdamdeo.todo.domain.TodoCommandHandler;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Objects;

@Path("/todos")
@Produces(MediaType.APPLICATION_JSON)
public class TodoEndpoint {

    final TodoCommandHandler todoCommandHandler;

    public TodoEndpoint(final TodoCommandHandler todoCommandHandler) {
        this.todoCommandHandler = Objects.requireNonNull(todoCommandHandler);
    }

    @POST
    @Path("/createNewTodo")
    @Consumes(MediaType.APPLICATION_JSON)
    public TodoDTO createNewTodo(final CreateNewTodoCommand createNewTodoCommand) throws Throwable {
        return new TodoDTO(todoCommandHandler.handle(createNewTodoCommand));
    }

    @POST
    @Path("/markTodoAsCompleted")
    @Consumes(MediaType.APPLICATION_JSON)
    public TodoDTO markTodoAsCompletedCommand(final MarkTodoAsCompletedCommand markTodoAsCompletedCommand) throws Throwable {
        return new TodoDTO(todoCommandHandler.handle(markTodoAsCompletedCommand));
    }

}
