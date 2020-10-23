package com.damdamdeo.todo.publicfrontend.interfaces;

import com.damdamdeo.todo.publicfrontend.domain.TodoStatus;
import com.damdamdeo.todo.publicfrontend.infrastructure.TodoQueryRemoteService;
import com.damdamdeo.todo.publicfrontend.infrastructure.TodoWriteRemoteService;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.*;

@QuarkusTest
public class TodoEndpointTest {

    @InjectMock
    @RestClient
    TodoWriteRemoteService todoWriteRemoteService;

    @InjectMock
    @RestClient
    TodoQueryRemoteService todoQueryRemoteService;

    // Oauth is not provided as I have mocked the service used to check user connected.
    // Can be considered as ugly.
    // create new todo

    @Test
    public void should_create_new_todo_from_write_remote_service() {
        // Given
        final TodoDTO todoDTO = new TodoDTO("todoId", "lorem ipsum", TodoStatus.IN_PROGRESS, Boolean.TRUE, 0l);
        doReturn(todoDTO).when(todoWriteRemoteService).createNewTodo("lorem ipsum");

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
                .body("canMarkTodoAsCompleted", equalTo(true));

        verify(todoWriteRemoteService, times(1)).createNewTodo(any());
        verifyNoMoreInteractions(todoWriteRemoteService, todoQueryRemoteService);
    }

    // mark todo as completed

    @Test
    public void should_mark_todo_as_completed_from_write_remote_service() {
        // Given
        final TodoDTO todoDTO = new TodoDTO("todoId", "lorem ipsum", TodoStatus.COMPLETED, Boolean.FALSE, 1l);
        doReturn(todoDTO).when(todoWriteRemoteService).markTodoAsCompleted("todoId");

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
                .body("canMarkTodoAsCompleted", equalTo(false));

        verify(todoWriteRemoteService, times(1)).markTodoAsCompleted(any());
        verifyNoMoreInteractions(todoWriteRemoteService, todoQueryRemoteService);
    }

    // get todo

    @Test
    public void should_get_todo_from_query_remote_service() {
        // Given
        final TodoDTO todoDTO = new TodoDTO("todoId", "lorem ipsum", TodoStatus.COMPLETED, Boolean.FALSE, 1l);
        doReturn(todoDTO).when(todoQueryRemoteService).getTodoByTodoId("todoId");

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

        verify(todoQueryRemoteService, times(1)).getTodoByTodoId(any());
        verifyNoMoreInteractions(todoWriteRemoteService, todoQueryRemoteService);
    }

    // list all todos

    @Test
    public void should_list_all_todos_from_query_remote_service() {
        // Given
        final TodoDTO todoDTO = new TodoDTO("todoId", "lorem ipsum", TodoStatus.COMPLETED, Boolean.FALSE, 1l);
        doReturn(Collections.singletonList(todoDTO)).when(todoQueryRemoteService).getAllTodos();

        // When && Then
        given()
                .get("/todos")
                .then()
                .statusCode(200)
                .body("$.size()", equalTo(1))
                .body("[0].todoId", equalTo("todoId"))
                .body("[0].description", equalTo("lorem ipsum"))
                .body("[0].todoStatus", equalTo("COMPLETED"))
                .body("[0].canMarkTodoAsCompleted", equalTo(false))
                .body("[0].version", equalTo(1));

        verify(todoQueryRemoteService, times(1)).getAllTodos();
        verifyNoMoreInteractions(todoWriteRemoteService, todoQueryRemoteService);
    }

}
