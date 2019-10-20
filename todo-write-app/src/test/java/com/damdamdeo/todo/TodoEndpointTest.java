package com.damdamdeo.todo;

import com.jayway.restassured.module.jsv.JsonSchemaValidator;
import io.quarkus.test.junit.QuarkusTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

@QuarkusTest
public class TodoEndpointTest extends AbstractTodoTest {

    @Test
    public void should_api_create_todo() {
        given()
                .param("todoId", "todoId")
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
        ;
    }

    @Test
    public void should_api_mark_todo_as_completed() {
        given()
                .param("todoId", "todoId")
                .param("description", "lorem ipsum")
                .when()
                .post("/todos/createNewTodo")
                .then()
                .statusCode(200);

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
        ;
    }

    @Test
    public void should_fail_when_todoId_already_affected() {
        given()
                .param("todoId", "todoId")
                .param("description", "lorem ipsum")
                .when()
                .post("/todos/createNewTodo")
                .then()
                .statusCode(200);
        given()
                .param("todoId", "todoId")
                .param("description", "lorem ipsum")
                .when()
                .post("/todos/createNewTodo")
                .then()
                .statusCode(409)
                .contentType(MediaType.TEXT_PLAIN)
                .body(Matchers.equalTo("Le todoId 'todoId' est déjà existant."));
    }

}
