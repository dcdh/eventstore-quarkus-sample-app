package com.damdamdeo.todo.interfaces;

import com.damdamdeo.todo.aggregate.TodoRepository;
import com.damdamdeo.todo.aggregate.UnknownTodoException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Optional;

@Path("/todos")
@Produces(MediaType.APPLICATION_JSON)
public class TodoEndpoint {

    final TodoRepository todoRepository;

    public TodoEndpoint(final TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @GET
    @Path("/{todoId}")
    public TodoDTO getTodo(@PathParam("todoId") final String todoId) {
        return Optional.ofNullable(todoRepository.get(todoId))
                .map(TodoDTO::new)
                .orElseThrow(() -> new UnknownTodoException(todoId));
    }

}
