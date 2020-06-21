package com.damdamdeo.todo.publicfrontend.infrastructure;

import com.damdamdeo.todo.publicfrontend.interfaces.TodoDTO;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/")
@ApplicationScoped
@RegisterRestClient(configKey="todo-write-api")
public interface TodoWriteRemoteService {

    @POST
    @Path("/todos/createNewTodo")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    TodoDTO createNewTodo(@FormParam("description") String description);

    @POST
    @Path("/todos/markTodoAsCompleted")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    TodoDTO markTodoAsCompleted(@FormParam("todoId") String todoId);

}
