package com.damdamdeo.todo.publicfrontend.infrastructure.interfaces;

import com.damdamdeo.todo.publicfrontend.infrastructure.TodoQueryRemoteService;
import com.damdamdeo.todo.publicfrontend.infrastructure.TodoWriteRemoteService;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/todos")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Todo") // When using this Tag I cannot do constructor injection :(
public class TodoEndpoint {

    // Aggregate all remote repositories
    // It is a passe-plat. We could removed parameters to transmit to front - for security reason, add to repositories user connected data ...
    // But we could also define message error from remote services. And to do that remote services should provide context variables as json in response.

    @Inject
    @RestClient
    TodoWriteRemoteService todoWriteRemoteService;

    @Inject
    @RestClient
    TodoQueryRemoteService todoQueryRemoteService;

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
