package com.damdamdeo.todo.infrastructure.interfaces;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRootRepository;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.UnknownAggregateRootException;
import com.damdamdeo.todo.domain.TodoAggregateRoot;
import com.damdamdeo.todo.domain.command.CreateNewTodoCommand;
import com.damdamdeo.todo.domain.command.MarkTodoAsCompletedCommand;
import com.damdamdeo.todo.domain.command.handler.CreateNewTodoCommandHandler;
import com.damdamdeo.todo.domain.command.handler.MarkTodoAsCompletedCommandHandler;
import com.damdamdeo.todo.domain.api.Todo;
import com.damdamdeo.todo.domain.api.TodoAlreadyExistentException;
import com.damdamdeo.todo.domain.api.TodoAlreadyMarkedAsCompletedException;
import com.damdamdeo.todo.domain.api.TodoStatus;
import com.jayway.restassured.module.jsv.JsonSchemaValidator;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import javax.inject.Named;
import javax.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class TodoEndpointTest {

    @InjectMock
    @Named("SingleExecutionCreateNewTodoCommandHandler")
    CreateNewTodoCommandHandler createNewTodoCommandHandler;

    @InjectMock
    @Named("SingleExecutionMarkTodoAsCompletedCommandHandler")
    MarkTodoAsCompletedCommandHandler markTodoAsCompletedCommandHandler;

    @InjectMock
    SecurityIdentity securityIdentity;

    @InjectMock
    AggregateRootRepository aggregateRootRepository;

    @Test
    public void should_api_creates_new_todo() throws Throwable {
        // Given
        doReturn(Boolean.TRUE).when(securityIdentity).hasRole("frontend-user");
        final TodoAggregateRoot todoAggregateRoot = mock(TodoAggregateRoot.class);
        doReturn("todoId").when(todoAggregateRoot).todoId();
        doReturn("lorem ipsum").when(todoAggregateRoot).description();
        doReturn(TodoStatus.IN_PROGRESS).when(todoAggregateRoot).todoStatus();
        doReturn(0l).when(todoAggregateRoot).version();
        doCallRealMethod().when(todoAggregateRoot).canMarkTodoAsCompletedSpecification();
        doReturn(todoAggregateRoot).when(createNewTodoCommandHandler)
                .execute(new CreateNewTodoCommand("lorem ipsum"));

        // When && Then
        given()
                .param("description", "lorem ipsum")
                .when()
                .post("/todos/createNewTodo")
                .then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("expected/todo.json"))
                .body("todoId", equalTo("todoId"))
                .body("description", equalTo("lorem ipsum"))
                .body("todoStatus", equalTo("IN_PROGRESS"))
                .body("version", equalTo(0))
                .body("canMarkTodoAsCompleted", equalTo(true))
        ;

        verify(securityIdentity, times(1)).hasRole(anyString());
        verify(createNewTodoCommandHandler, times(1)).execute(any(CreateNewTodoCommand.class));
        verify(todoAggregateRoot, times(1)).todoId();
        verify(todoAggregateRoot, times(1)).description();
        verify(todoAggregateRoot, times(2)).todoStatus();
        verify(todoAggregateRoot, times(1)).version();
        verify(todoAggregateRoot, times(1)).canMarkTodoAsCompletedSpecification();
    }

    @Test
    public void should_not_be_authorized_to_create_new_todo_when_user_is_not_authenticated() {
        // Given
        doReturn(Boolean.TRUE).when(securityIdentity).isAnonymous();

        // When && Then
        given()
                .param("description", "lorem ipsum")
                .when()
                .post("/todos/createNewTodo")
                .then()
                .statusCode(401);

        verify(securityIdentity, times(1)).hasRole(anyString());
        verify(securityIdentity, atLeastOnce()).isAnonymous();
    }

    @Test
    public void should_api_fails_when_creating_an_already_existent_todo() throws Throwable {
        // Given
        doReturn(Boolean.TRUE).when(securityIdentity).hasRole("frontend-user");
        final Todo todo = mock(Todo.class);
        doReturn("todoId").when(todo).todoId();
        doThrow(new TodoAlreadyExistentException(todo))
                .when(createNewTodoCommandHandler)
                .execute(any(CreateNewTodoCommand.class));

        // When && Then
        given()
                .param("description", "lorem ipsum")
                .when()
                .post("/todos/createNewTodo")
                .then()
                .statusCode(409)
                .contentType(MediaType.TEXT_PLAIN)
                .body(Matchers.equalTo("Le todoId 'todoId' est déjà existant."));
        verify(securityIdentity, times(1)).hasRole(anyString());
        verify(createNewTodoCommandHandler, times(1)).execute(any(CreateNewTodoCommand.class));
        verify(todo, times(1)).todoId();
    }

    @Test
    public void should_api_mark_todo_as_completed() throws Throwable {
        // Given
        doReturn(Boolean.TRUE).when(securityIdentity).hasRole("frontend-user");
        final TodoAggregateRoot todoAggregateRoot = mock(TodoAggregateRoot.class);
        doReturn("todoId").when(todoAggregateRoot).todoId();
        doReturn("lorem ipsum").when(todoAggregateRoot).description();
        doReturn(TodoStatus.COMPLETED).when(todoAggregateRoot).todoStatus();
        doReturn(1l).when(todoAggregateRoot).version();
        doCallRealMethod().when(todoAggregateRoot).canMarkTodoAsCompletedSpecification();
        doReturn(todoAggregateRoot).when(markTodoAsCompletedCommandHandler)
                .execute(new MarkTodoAsCompletedCommand("todoId"));

        // When && Then
        given()
                .param("todoId", "todoId")
                .when()
                .post("/todos/markTodoAsCompleted")
                .then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("expected/todo.json"))
                .body("todoId", equalTo("todoId"))
                .body("description", equalTo("lorem ipsum"))
                .body("todoStatus", equalTo("COMPLETED"))
                .body("version", equalTo(1))
                .body("canMarkTodoAsCompleted", equalTo(false))
        ;

        verify(securityIdentity, times(1)).hasRole(anyString());
        verify(markTodoAsCompletedCommandHandler, times(1)).execute(any(MarkTodoAsCompletedCommand.class));
        verify(todoAggregateRoot, times(1)).todoId();
        verify(todoAggregateRoot, times(1)).description();
        verify(todoAggregateRoot, times(2)).todoStatus();
        verify(todoAggregateRoot, times(1)).version();
        verify(todoAggregateRoot, times(1)).canMarkTodoAsCompletedSpecification();
    }

    @Test
    public void should_not_be_authorized_to_mark_todo_as_completed_when_user_has_not_the_role_frontend_user() {
        // Given
        doReturn(Boolean.FALSE).when(securityIdentity).hasRole("frontend-user");

        // When && Then
        given()
                .param("todoId", "todoId")
                .when()
                .post("/todos/markTodoAsCompleted")
                .then()
                .statusCode(403);

        verify(securityIdentity, times(1)).hasRole(anyString());
        verify(securityIdentity, atLeastOnce()).isAnonymous();
    }

    @Test
    public void should_api_fails_when_marking_as_completed_an_unknown_todo() throws Throwable {
        // Given
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);
        doReturn("todoId").when(aggregateRootId).aggregateRootId();
        doReturn(Boolean.TRUE).when(securityIdentity).hasRole("frontend-user");
        doThrow(new UnknownAggregateRootException(aggregateRootId))
                .when(markTodoAsCompletedCommandHandler)
                .execute(any(MarkTodoAsCompletedCommand.class));

        // When && Then
        given()
                .param("todoId", "todoId")
                .when()
                .post("/todos/markTodoAsCompleted")
                .then()
                .statusCode(404)
                .contentType(MediaType.TEXT_PLAIN)
                .body(Matchers.equalTo("Le todoId 'todoId' est inconnu."));
        verify(securityIdentity, times(1)).hasRole(anyString());
        verify(aggregateRootId, times(1)).aggregateRootId();
        verify(markTodoAsCompletedCommandHandler, times(1)).execute(any(MarkTodoAsCompletedCommand.class));
    }

    @Test
    public void should_api_fails_when_mark_todo_already_completed_as_completed() throws Throwable {
        // Given
        doReturn(Boolean.TRUE).when(securityIdentity).hasRole("frontend-user");
        final Todo todo = mock(Todo.class);
        doReturn("todoId").when(todo).todoId();
        doThrow(new TodoAlreadyMarkedAsCompletedException(todo))
                .when(markTodoAsCompletedCommandHandler)
                .execute(new MarkTodoAsCompletedCommand("todoId"));

        // When && Then
        given()
                .param("todoId", "todoId")
                .when()
                .post("/todos/markTodoAsCompleted")
                .then()
                .statusCode(409)
                .contentType(MediaType.TEXT_PLAIN)
                .body(Matchers.equalTo("Le todo 'todoId' est déjà complété."));
        verify(securityIdentity, times(1)).hasRole(anyString());
        verify(markTodoAsCompletedCommandHandler, times(1)).execute(any(MarkTodoAsCompletedCommand.class));
        verify(todo, times(1)).todoId();
    }

    @Test
    public void should_api_get_the_materialized_state() {
        // Given
        doReturn(Boolean.TRUE).when(securityIdentity).hasRole("frontend-user");
        final TodoAggregateRoot todoAggregateRoot = mock(TodoAggregateRoot.class);
        doReturn("todoId").when(todoAggregateRoot).todoId();
        doReturn("lorem ipsum").when(todoAggregateRoot).description();
        doReturn(TodoStatus.COMPLETED).when(todoAggregateRoot).todoStatus();
        doReturn(1l).when(todoAggregateRoot).version();
        doCallRealMethod().when(todoAggregateRoot).canMarkTodoAsCompletedSpecification();
        doReturn(todoAggregateRoot).when(aggregateRootRepository).findMaterializedState("todoId", TodoAggregateRoot.class);

        // When && Then
        given()
                .when()
                .get("/todos/todoId")
                .then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("expected/todo.json"))
                .body("todoId", equalTo("todoId"))
                .body("description", equalTo("lorem ipsum"))
                .body("todoStatus", equalTo("COMPLETED"))
                .body("version", equalTo(1))
                .body("canMarkTodoAsCompleted", equalTo(false));

        verify(aggregateRootRepository, times(1)).findMaterializedState(any(), any());
        verify(securityIdentity, times(1)).hasRole(anyString());
        verify(todoAggregateRoot, times(1)).todoId();
        verify(todoAggregateRoot, times(1)).description();
        verify(todoAggregateRoot, times(2)).todoStatus();
        verify(todoAggregateRoot, times(1)).version();
        verify(todoAggregateRoot, times(1)).canMarkTodoAsCompletedSpecification();
    }

    @Test
    public void should_api_fails_when_getting_the_materialized_state_for_an_unknown_todo() {
        // Given
        doReturn(Boolean.TRUE).when(securityIdentity).hasRole("frontend-user");
        final AggregateRootId unknownAggregateId = mock(AggregateRootId.class);
        doReturn("todoId").when(unknownAggregateId).aggregateRootId();
        doThrow(new UnknownAggregateRootException(unknownAggregateId)).when(aggregateRootRepository)
                .findMaterializedState(any(), any());

        // When && Then
        given()
                .when()
                .get("/todos/todoId")
                .then()
                .statusCode(404)
                .contentType(MediaType.TEXT_PLAIN)
                .body(Matchers.equalTo("Le todoId 'todoId' est inconnu."));

        verify(aggregateRootRepository, times(1)).findMaterializedState(any(), any());
        verify(securityIdentity, times(1)).hasRole(anyString());
    }

    @Test
    public void should_not_be_authorized_to_get_the_materialized_state_when_user_is_not_authenticated() {
        // Given
        doReturn(Boolean.FALSE).when(securityIdentity).hasRole("frontend-user");

        // When && Then
        given()
                .when()
                .get("/todos/todoId")
                .then()
                .statusCode(403);

        verify(securityIdentity, times(1)).hasRole(anyString());
        verify(securityIdentity, atLeastOnce()).isAnonymous();
    }

}
