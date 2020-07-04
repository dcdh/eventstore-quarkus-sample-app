package com.damdamdeo.todo.publicfrontend.interfaces;

import com.damdamdeo.todo.publicfrontend.domain.user.UnexpectedException;
import com.damdamdeo.todo.publicfrontend.domain.user.UserRegistrationRemoteService;
import com.damdamdeo.todo.publicfrontend.domain.user.UsernameOrEmailAlreadyUsedException;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;

@QuarkusTest
public class UserRegistrationEndpointTest {

    @InjectMock
    UserRegistrationRemoteService userRegistrationRemoteService;

    @BeforeEach
    @AfterEach
    public void flush() {
        reset(userRegistrationRemoteService);
    }

    @Test
    public void should_call_user_registration_remote_service() throws Exception {
        // Given

        // When && Then
        given()
                .formParams("username", "username",
                        "password", "password",
                        "email", "email")
                .when()
                .post("/registration")
                .then()
                .statusCode(204);
        verify(userRegistrationRemoteService, times(1)).register("username", "password", "email");
        verifyNoMoreInteractions(userRegistrationRemoteService);
    }

    @Test
    public void should_return_expected_response_when_registering_a_new_user_where_username_or_email_already_exist() throws Exception {
        // Given
        doThrow(new UsernameOrEmailAlreadyUsedException("username", "email")).when(userRegistrationRemoteService)
                .register("username", "password", "email");

        // When && Then
        given()
                .formParams("username", "username",
                        "password", "password",
                        "email", "email")
                .when()
                .post("/registration")
                .then()
                .statusCode(409)
                .contentType("text/plain")
                .body(equalTo("Username 'username' or email 'email' already used"));
        verify(userRegistrationRemoteService, times(1)).register("username", "password", "email");
        verifyNoMoreInteractions(userRegistrationRemoteService);
    }

    @Test
    public void should_return_expected_response_when_registering_a_new_user_fails_unexpectedly() throws Exception {
        // Given
        doThrow(new UnexpectedException(500)).when(userRegistrationRemoteService)
                .register("username", "password", "email");

        // When && Then
        given()
                .formParams("username", "username",
                        "password", "password",
                        "email", "email")
                .when()
                .post("/registration")
                .then()
                .statusCode(500)
                .contentType("text/plain")
                .body(equalTo("Unexpected exception"));
        verify(userRegistrationRemoteService, times(1)).register("username", "password", "email");
        verifyNoMoreInteractions(userRegistrationRemoteService);
    }

}
