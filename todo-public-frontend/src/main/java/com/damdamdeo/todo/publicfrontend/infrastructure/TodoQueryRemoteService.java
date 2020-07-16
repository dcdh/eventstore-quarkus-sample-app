package com.damdamdeo.todo.publicfrontend.infrastructure;

import com.damdamdeo.todo.publicfrontend.interfaces.TodoDTO;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Path("/")
@ApplicationScoped
@RegisterRestClient(configKey="todo-query-api")
public interface TodoQueryRemoteService {

    @GET
    @Path("/todos")
    @Produces(MediaType.APPLICATION_JSON)
    List<TodoDTO> getAllTodos(@HeaderParam("Authorization") String bearer);

    @GET
    @Path("/todos/{todoId}")
    @Produces(MediaType.APPLICATION_JSON)
    TodoDTO getTodoByTodoId(@HeaderParam("Authorization") String bearer, @PathParam("todoId") String todoId);

}
