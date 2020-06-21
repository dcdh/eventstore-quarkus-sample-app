package com.damdamdeo.todo.interfaces;

import com.damdamdeo.todo.domain.TodoRepository;
import com.damdamdeo.todo.domain.api.Todo;
import com.damdamdeo.todo.domain.api.TodoStatus;
import com.jayway.restassured.module.jsv.JsonSchemaValidator;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.*;

@QuarkusTest
public class TodoEndpointTest {

    @InjectMock
    TodoRepository todoRepository;

    @Test
    public void should_return_todo() {
        // Given
        final Todo todo = mock(Todo.class);
        doReturn("todoId").when(todo).todoId();
        doReturn("lorem ipsum").when(todo).description();
        doReturn(TodoStatus.IN_PROGRESS).when(todo).todoStatus();
        doReturn(0l).when(todo).version();
        doCallRealMethod().when(todo).canMarkTodoAsCompletedSpecification();

        doReturn(todo).when(todoRepository).get("todoId");

        // When && Then
        given()
                .get("/todos/todoId")
                .then()
                .log().all()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("expected/todo.json"))
                .body("todoId", equalTo("todoId"))
                .body("description", equalTo("lorem ipsum"))
                .body("todoStatus", equalTo("IN_PROGRESS"))
                .body("canMarkTodoAsCompleted", equalTo(true))
                .body("version", equalTo(0));

        verify(todo, atLeastOnce()).todoId();
        verify(todo, times(1)).description();
        verify(todo, atLeastOnce()).todoStatus();
        verify(todo, times(1)).version();
        verify(todo, times(1)).canMarkTodoAsCompletedSpecification();
        verify(todoRepository, times(1)).get(any());
        verifyNoMoreInteractions(todoRepository, todo);
    }

    @Test
    public void should_api_fail_when_retrieving_unknown_todo() {
        // Given
        doReturn(null).when(todoRepository).get("todoId");

        // When && Then
        given()
                .when()
                .get("/todos/todoId")
                .then()
                .statusCode(404)
                .contentType(MediaType.TEXT_PLAIN)
                .body(Matchers.equalTo("Le todoId 'todoId' est inconnu."));
        verify(todoRepository, times(1)).get(any());
        verifyNoMoreInteractions(todoRepository);
    }

    @Test
    public void should_list_all_todos() {
        // Given
        final Todo todo = mock(Todo.class);
        doReturn("todoId").when(todo).todoId();
        doReturn("lorem ipsum").when(todo).description();
        doReturn(TodoStatus.IN_PROGRESS).when(todo).todoStatus();
        doReturn(0l).when(todo).version();
        doCallRealMethod().when(todo).canMarkTodoAsCompletedSpecification();

        doReturn(Collections.singletonList(todo)).when(todoRepository).fetchAll();

        // When && Then
        given()
                .get("/todos")
                .then()
                .log().all()
                .statusCode(200)
                .body("$.size()", equalTo(1))
                .body("[0].todoId", equalTo("todoId"))
                .body("[0].description", equalTo("lorem ipsum"))
                .body("[0].todoStatus", equalTo("IN_PROGRESS"))
                .body("[0].canMarkTodoAsCompleted", equalTo(true))
                .body("[0].version", equalTo(0));

        verify(todo, atLeastOnce()).todoId();
        verify(todo, times(1)).description();
        verify(todo, atLeastOnce()).todoStatus();
        verify(todo, times(1)).version();
        verify(todoRepository, times(1)).fetchAll();
        verify(todo, times(1)).canMarkTodoAsCompletedSpecification();
        verifyNoMoreInteractions(todoRepository, todo);
    }

}
