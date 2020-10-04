package com.damdamdeo.todo.infrastructure.interfaces;

import com.damdamdeo.todo.domain.TodoDomainRepository;

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

    private final TodoDomainRepository todoDomainRepository;

    public TodoEndpoint(final TodoDomainRepository todoDomainRepository) {
        this.todoDomainRepository = todoDomainRepository;
    }

    @RolesAllowed("frontend-user")
    @GET
    @Path("/{todoId}")
    public TodoDTO getTodo(@PathParam("todoId") final String todoId) {
        return Optional.of(todoDomainRepository.get(todoId))
                .map(TodoDTO::new)
                .get();
    }

    @RolesAllowed("frontend-user")
    @GET
    public List<TodoDTO> listAllTodos() {
        return todoDomainRepository.fetchAll()
                .stream()
                .map(TodoDTO::new)
                .collect(Collectors.toList());
    }

}
