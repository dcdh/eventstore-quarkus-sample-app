package com.damdamdeo.todo.interfaces;

import com.damdamdeo.todo.domain.TodoRepository;
import com.damdamdeo.todo.domain.api.UnknownTodoException;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Path("/todos")
@Produces(MediaType.APPLICATION_JSON)
public class TodoEndpoint {

    final TodoRepository todoRepository;

    public TodoEndpoint(final TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @RolesAllowed("frontend-user")
    @GET
    @Path("/{todoId}")
    public TodoDTO getTodo(@PathParam("todoId") final String todoId) {
        return Optional.ofNullable(todoRepository.get(todoId))
                .map(TodoDTO::new)
                .orElseThrow(() -> new UnknownTodoException(todoId));
    }

    @RolesAllowed("frontend-user")
    @GET
    public List<TodoDTO> listAllTodos() {
        return todoRepository.fetchAll()
                .stream()
                .map(TodoDTO::new)
                .collect(Collectors.toList());
    }

}
