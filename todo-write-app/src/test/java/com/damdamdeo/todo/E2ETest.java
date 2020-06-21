package com.damdamdeo.todo;

import com.damdamdeo.todo.infrastructure.UUIDTodoIdGenerator;
import com.jayway.restassured.module.jsv.JsonSchemaValidator;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.doReturn;

@QuarkusTest
public class E2ETest extends AbstractTodoTest {

    @InjectMock
    UUIDTodoIdGenerator uuidTodoIdGenerator;

    @BeforeEach
    public void setupInjectedMock() {
        doReturn("todoId").when(uuidTodoIdGenerator).generateTodoId();
    }

    @Test
    public void should_api_create_todo() {
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
        // TODO checker que l'event est présent et le contenu !
    }

    @Test
    public void should_api_mark_todo_as_completed() {
        given()
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
                .body("canMarkTodoAsCompleted", equalTo(false))
        // TODO checker que l'event est présent et le contenu !
        ;
    }

}
