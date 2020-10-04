package com.damdamdeo.todo;

import com.damdamdeo.todo.infrastructure.UUIDTodoIdGenerator;
import com.jayway.restassured.module.jsv.JsonSchemaValidator;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.restassured.RestAssured;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.AccessTokenResponse;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.mockito.Mockito.doReturn;

@QuarkusTest
public class E2ETest extends AbstractTodoTest {

    @InjectMock
    UUIDTodoIdGenerator uuidTodoIdGenerator;

    private static final String USERNAME_TO_CONNECT_WITH = "damdamdeo";
    private static final String USERNAME_PASSWORD = "damdamdeo";

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String keyCloakServerAuthUrl;

    @BeforeEach
    public void setupInjectedMock() {
        doReturn("todoId").when(uuidTodoIdGenerator).generateTodoId();
    }

    @Test
    public void should_api_create_todo() {
        given()
                .auth().oauth2(getAccessToken())
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

        given()
                .auth().oauth2(getAccessToken())
                .when()
                .get("/todos/todoId")
                .then()
                .statusCode(200)
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("expected/todo.json"))
                .body("todoId", equalTo("todoId"))
                .body("description", equalTo("lorem ipsum"))
                .body("todoStatus", equalTo("IN_PROGRESS"))
                .body("version", equalTo(0))
                .body("canMarkTodoAsCompleted", equalTo(true));
    }

    @Test
    public void should_api_mark_todo_as_completed() {
        given()
                .auth().oauth2(getAccessToken())
                .param("description", "lorem ipsum")
                .when()
                .post("/todos/createNewTodo")
                .then()
                .statusCode(200);

        given()
                .auth().oauth2(getAccessToken())
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
        given()
                .auth().oauth2(getAccessToken())
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

        // TODO checker que l'event est présent et le contenu !
    }

    private String getAccessToken() {
        return RestAssured
                .given()
                .param("grant_type", "password")
                .param("username", USERNAME_TO_CONNECT_WITH)
                .param("password", USERNAME_PASSWORD)
                .param("client_id", "todo-platform")
                .param("client_secret", "secret")
                .when()
                .post(keyCloakServerAuthUrl + "/protocol/openid-connect/token")
                .as(AccessTokenResponse.class).getToken();
    }

}
