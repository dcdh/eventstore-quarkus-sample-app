package com.damdamdeo.todo.interfaces;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.UnknownAggregateRootException;
import com.damdamdeo.todo.aggregate.TodoAggregateRoot;
import com.damdamdeo.todo.command.CreateNewTodoCommand;
import com.damdamdeo.todo.command.MarkTodoAsCompletedCommand;
import com.damdamdeo.todo.command.handler.CreateNewTodoCommandHandler;
import com.damdamdeo.todo.command.handler.MarkTodoAsCompletedCommandHandler;
import com.damdamdeo.todo.domain.api.Todo;
import com.damdamdeo.todo.domain.api.TodoAlreadyExistentException;
import com.damdamdeo.todo.domain.api.TodoAlreadyMarkedAsCompletedException;
import com.damdamdeo.todo.domain.api.TodoStatus;
import com.jayway.restassured.module.jsv.JsonSchemaValidator;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class TodoEndpointTest {

    @InjectMock
    CreateNewTodoCommandHandler createNewTodoCommandHandler;

    @InjectMock
    MarkTodoAsCompletedCommandHandler markTodoAsCompletedCommandHandler;

    @Test
    public void should_api_creates_new_todo() throws Throwable {
        // Given
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

        verify(createNewTodoCommandHandler, times(1)).execute(any(CreateNewTodoCommand.class));
        verify(todoAggregateRoot, times(1)).todoId();
        verify(todoAggregateRoot, times(1)).description();
        verify(todoAggregateRoot, times(2)).todoStatus();
        verify(todoAggregateRoot, times(1)).version();
        verify(todoAggregateRoot, times(1)).canMarkTodoAsCompletedSpecification();
        verifyNoMoreInteractions(createNewTodoCommandHandler, markTodoAsCompletedCommandHandler, todoAggregateRoot);
    }

    @Test
    public void should_api_fails_when_creating_an_already_existent_todo() throws Throwable {
        // Given
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
        verify(createNewTodoCommandHandler, times(1)).execute(any(CreateNewTodoCommand.class));
        verify(todo, times(1)).todoId();
        verifyNoMoreInteractions(createNewTodoCommandHandler, markTodoAsCompletedCommandHandler, todo);
    }

    @Test
    public void should_api_mark_todo_as_completed() throws Throwable {
        // Given
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

        verify(markTodoAsCompletedCommandHandler, times(1)).execute(any(MarkTodoAsCompletedCommand.class));
        verify(todoAggregateRoot, times(1)).todoId();
        verify(todoAggregateRoot, times(1)).description();
        verify(todoAggregateRoot, times(2)).todoStatus();
        verify(todoAggregateRoot, times(1)).version();
        verify(todoAggregateRoot, times(1)).canMarkTodoAsCompletedSpecification();
        verifyNoMoreInteractions(createNewTodoCommandHandler, markTodoAsCompletedCommandHandler, todoAggregateRoot);
    }

    @Test
    public void should_api_fails_when_marking_as_completed_an_unknown_todo() throws Throwable {
        // Given
        doThrow(new UnknownAggregateRootException("todoId"))
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
        verify(markTodoAsCompletedCommandHandler, times(1)).execute(any(MarkTodoAsCompletedCommand.class));
        verifyNoMoreInteractions(createNewTodoCommandHandler, markTodoAsCompletedCommandHandler);
    }

    @Test
    public void should_api_fails_when_mark_todo_already_completed_as_completed() throws Throwable {
        // Given
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
        verify(markTodoAsCompletedCommandHandler, times(1)).execute(any(MarkTodoAsCompletedCommand.class));
        verify(todo, times(1)).todoId();
        verifyNoMoreInteractions(createNewTodoCommandHandler, todo, markTodoAsCompletedCommandHandler);
    }

}