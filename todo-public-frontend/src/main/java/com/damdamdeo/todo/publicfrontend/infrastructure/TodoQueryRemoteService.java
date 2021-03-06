package com.damdamdeo.todo.publicfrontend.infrastructure;

import com.damdamdeo.todo.publicfrontend.infrastructure.interfaces.TodoDTO;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Path("/")
@ApplicationScoped
@RegisterRestClient(configKey="todo-query-api")
@RegisterClientHeaders
public interface TodoQueryRemoteService {

    @GET
    @Path("/todos")
    @Produces(MediaType.APPLICATION_JSON)
    List<TodoDTO> getAllTodos();

    @GET
    @Path("/todos/{todoId}")
    @Produces(MediaType.APPLICATION_JSON)
    TodoDTO getTodoByTodoId(@PathParam("todoId") String todoId);

}
