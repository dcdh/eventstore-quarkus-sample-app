package com.damdamdeo.todo.interfaces;

import com.damdamdeo.eventdataspreader.writeside.command.api.CommandHandlerExecutor;
import com.damdamdeo.todo.domain.TodoAggregateRoot;
import com.damdamdeo.todo.command.CreateNewTodoCommand;
import com.damdamdeo.todo.command.MarkTodoAsCompletedCommand;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Objects;

@Path("/todos")
@Produces(MediaType.APPLICATION_JSON)
public class TodoEndpoint {

    final CommandHandlerExecutor commandHandlerExecutor;

    public TodoEndpoint(final CommandHandlerExecutor commandHandlerExecutor) {
        this.commandHandlerExecutor = Objects.requireNonNull(commandHandlerExecutor);
    }

    @POST
    @Path("/createNewTodo")
    @Produces(MediaType.APPLICATION_JSON)
    public TodoDTO createNewTodo(@FormParam("todoId") final String todoId,
                                 @FormParam("description") final String description) throws Throwable {
        return new TodoDTO((TodoAggregateRoot) commandHandlerExecutor.execute(new CreateNewTodoCommand(todoId, description)).get());
    }

    @POST
    @Path("/markTodoAsCompleted")
    @Produces(MediaType.APPLICATION_JSON)
    public TodoDTO markTodoAsCompletedCommand(@FormParam("todoId") final String todoId) throws Throwable {
        return new TodoDTO((TodoAggregateRoot) commandHandlerExecutor.execute(new MarkTodoAsCompletedCommand(todoId)).get());
    }

}
