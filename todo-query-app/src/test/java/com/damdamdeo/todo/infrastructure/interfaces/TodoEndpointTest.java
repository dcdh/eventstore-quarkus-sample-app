package com.damdamdeo.todo.infrastructure.interfaces;

import com.damdamdeo.todo.domain.TodoDomain;
import com.damdamdeo.todo.domain.TodoDomainRepository;
import com.damdamdeo.todo.domain.api.TodoStatus;
import com.damdamdeo.todo.domain.api.UnknownTodoException;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.quarkus.security.identity.SecurityIdentity;
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
    TodoDomainRepository todoDomainRepository;

    @InjectMock
    SecurityIdentity securityIdentity;

    @Test
    public void should_return_todo() {
        // Given
        final TodoDomain todoDomain = TodoDomain.newBuilder()
                .withTodoId("todoId")
                .withDescription("lorem ipsum")
                .withTodoStatus(TodoStatus.IN_PROGRESS)
                .withVersion(0l)
                .build();

        doReturn(Boolean.TRUE).when(securityIdentity).hasRole("frontend-user");
        doReturn(todoDomain).when(todoDomainRepository).get("todoId");

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

        verify(securityIdentity, times(1)).hasRole(anyString());
        verify(todoDomainRepository, times(1)).get(any());
        verifyNoMoreInteractions(securityIdentity, todoDomainRepository);
    }

    @Test
    public void should_not_be_authorized_to_get_todo_when_user_is_not_authenticated() {
        // Given
        doReturn(Boolean.TRUE).when(securityIdentity).isAnonymous();

        // When && Then
        given()
                .when()
                .get("/todos/todoId")
                .then()
                .statusCode(401);

        verify(securityIdentity, times(1)).hasRole(anyString());
        verify(securityIdentity, times(1)).isAnonymous();
        verifyNoMoreInteractions(securityIdentity);
    }

    @Test
    public void should_api_fail_when_retrieving_unknown_todo() {
        // Given
        doThrow(new UnknownTodoException("todoId")).when(todoDomainRepository).get("todoId");
        doReturn(Boolean.TRUE).when(securityIdentity).hasRole("frontend-user");

        // When && Then
        given()
                .when()
                .get("/todos/todoId")
                .then()
                .statusCode(404)
                .contentType(MediaType.TEXT_PLAIN)
                .body(Matchers.equalTo("Le todoId 'todoId' est inconnu."));
        verify(securityIdentity, times(1)).hasRole(anyString());
        verify(todoDomainRepository, times(1)).get(any());
        verifyNoMoreInteractions(securityIdentity, todoDomainRepository);
    }

    @Test
    public void should_list_all_todos() {
        // Given
        final TodoDomain todoDomain = TodoDomain.newBuilder()
                .withTodoId("todoId")
                .withDescription("lorem ipsum")
                .withTodoStatus(TodoStatus.IN_PROGRESS)
                .withVersion(0l)
                .build();

        doReturn(Collections.singletonList(todoDomain)).when(todoDomainRepository).fetchAll();
        doReturn(Boolean.TRUE).when(securityIdentity).hasRole("frontend-user");

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

        verify(securityIdentity, times(1)).hasRole(anyString());
        verify(todoDomainRepository, times(1)).fetchAll();
        verifyNoMoreInteractions(securityIdentity, todoDomainRepository);
    }

    @Test
    public void should_not_be_authorized_to_list_all_todos_when_user_has_not_the_role_frontend_user() {
        // Given
        doReturn(Boolean.FALSE).when(securityIdentity).hasRole("frontend-user");

        // When && Then
        given()
                .get("/todos")
                .then()
                .statusCode(403);

        verify(securityIdentity, times(1)).hasRole(anyString());
        verify(securityIdentity, times(1)).isAnonymous();
        verifyNoMoreInteractions(securityIdentity, todoDomainRepository);
    }

    @Test
    public void should_not_be_authorized_to_list_all_todos_when_user_is_not_authenticated() {
        // Given
        doReturn(Boolean.TRUE).when(securityIdentity).isAnonymous();

        // When && Then
        given()
                .get("/todos")
                .then()
                .statusCode(401);

        verify(securityIdentity, times(1)).hasRole(anyString());
        verify(securityIdentity, times(1)).isAnonymous();
        verifyNoMoreInteractions(securityIdentity, todoDomainRepository);
    }

}