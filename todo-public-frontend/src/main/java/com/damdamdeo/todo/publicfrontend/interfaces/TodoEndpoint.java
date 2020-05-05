package com.damdamdeo.todo.publicfrontend.interfaces;

import com.damdamdeo.todo.publicfrontend.infrastructure.TodoQueryRemoteService;
import com.damdamdeo.todo.publicfrontend.infrastructure.TodoWriteRemoteService;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Objects;

@Path("/todos")
@Produces(MediaType.APPLICATION_JSON)
public class TodoEndpoint {

    // Aggregate all remote repositories
    // It is a passe-plat. We could removed parameters to transmit to front - for security reason, add to repositories user connected data ...
    // But we could also define message error from remote services. And to do that remote services should provide context variables as json in response.

    final TodoWriteRemoteService todoWriteRemoteService;

    final TodoQueryRemoteService todoQueryRemoteService;

    public TodoEndpoint(@RestClient final TodoWriteRemoteService todoWriteRemoteService,
                        @RestClient final TodoQueryRemoteService todoQueryRemoteService) {
        this.todoWriteRemoteService = Objects.requireNonNull(todoWriteRemoteService);
        this.todoQueryRemoteService = Objects.requireNonNull(todoQueryRemoteService);
    }

    @POST
    @Path("/createNewTodo")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public TodoDTO createNewTodo(@FormParam("description") final String description) throws Throwable {
        return todoWriteRemoteService.createNewTodo(description);
    }

    @POST
    @Path("/markTodoAsCompleted")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public TodoDTO markTodoAsCompletedCommand(@FormParam("todoId") final String todoId) throws Throwable {
        return todoWriteRemoteService.markTodoAsCompleted(todoId);
    }

    @GET
    @Path("/{todoId}")
    public TodoDTO getTodo(@PathParam("todoId") final String todoId) {
        return todoQueryRemoteService.getTodoByTodoId(todoId);
    }

    @GET
    public List<TodoDTO> listAllTodos() {
        return todoQueryRemoteService.getAllTodos();
    }

}
