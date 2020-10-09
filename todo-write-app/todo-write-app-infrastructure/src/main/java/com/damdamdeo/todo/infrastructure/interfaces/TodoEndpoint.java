package com.damdamdeo.todo.infrastructure.interfaces;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRootRepository;
import com.damdamdeo.todo.domain.TodoAggregateRoot;
import com.damdamdeo.todo.domain.command.CreateNewTodoCommand;
import com.damdamdeo.todo.domain.command.MarkTodoAsCompletedCommand;
import com.damdamdeo.todo.domain.command.handler.CreateNewTodoCommandHandler;
import com.damdamdeo.todo.domain.command.handler.MarkTodoAsCompletedCommandHandler;

import javax.annotation.security.RolesAllowed;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Objects;
import java.util.Optional;

@Path("/todos")
@Produces(MediaType.APPLICATION_JSON)
public class TodoEndpoint {

    final CreateNewTodoCommandHandler createNewTodoCommandHandler;

    final MarkTodoAsCompletedCommandHandler markTodoAsCompletedCommandHandler;

    final AggregateRootRepository aggregateRootRepository;

    public TodoEndpoint(@Named("SingleExecutionCreateNewTodoCommandHandler") final CreateNewTodoCommandHandler createNewTodoCommandHandler,
                        @Named("SingleExecutionMarkTodoAsCompletedCommandHandler") final MarkTodoAsCompletedCommandHandler markTodoAsCompletedCommandHandler,
                        final AggregateRootRepository aggregateRootRepository) {
        this.createNewTodoCommandHandler = Objects.requireNonNull(createNewTodoCommandHandler);
        this.markTodoAsCompletedCommandHandler = Objects.requireNonNull(markTodoAsCompletedCommandHandler);
        this.aggregateRootRepository = Objects.requireNonNull(aggregateRootRepository);
    }

    @RolesAllowed("frontend-user")
    @POST
    @Path("/createNewTodo")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public TodoDTO createNewTodo(@FormParam("description") final String description) throws Throwable {
        return new TodoDTO(createNewTodoCommandHandler.execute(new CreateNewTodoCommand(description)));
    }

    @RolesAllowed("frontend-user")
    @POST
    @Path("/markTodoAsCompleted")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public TodoDTO markTodoAsCompletedCommand(@FormParam("todoId") final String todoId) throws Throwable {
        return new TodoDTO(markTodoAsCompletedCommandHandler.execute(new MarkTodoAsCompletedCommand(todoId)));
    }

    @RolesAllowed("frontend-user")
    @GET
    @Path("/{todoId}")
    public TodoDTO getTodo(@PathParam("todoId") final String todoId) throws Throwable {
        return Optional.of(aggregateRootRepository.findMaterializedState(todoId, TodoAggregateRoot.class))
                .map(TodoDTO::new)
                .get();
    }

}