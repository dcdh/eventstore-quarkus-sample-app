package com.damdamdeo.todo.publicfrontend.interfaces;

import com.damdamdeo.todo.publicfrontend.domain.user.UserLoginRemoteService;
import com.damdamdeo.todo.publicfrontend.domain.user.UsernameOrPasswordInvalidException;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@QuarkusTest
public class AuthenticationEndpointTest {

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String keyCloakServerAuthUrl;

    @InjectSpy
    UserLoginRemoteService userLoginRemoteService;

    @ParameterizedTest
    @ValueSource(strings = {"damdamdeo/damdamdeo", "roger/roger"})
    public void should_login(final String usernamePassorwd) throws Exception {
        // Given
        final String username = usernamePassorwd.split("/")[0];
        final String password = usernamePassorwd.split("/")[1];

        // When && Then
        given()
                .formParams("username", username,
                        "password", password)
                .when()
                .post("/users/login")
                .then()
                .statusCode(200)
                .body("accessToken", notNullValue())
                .body("expiresIn", notNullValue())
                .body("refreshToken", notNullValue())
                .body("refreshExpiresIn", notNullValue());
        verify(userLoginRemoteService, times(1)).login(username, password);
        verifyNoMoreInteractions(userLoginRemoteService);
    }

    @Test
    public void should_return_expected_response_when_logging_using_a_wrong_username_or_password() throws Exception {
        // Given
        doThrow(new UsernameOrPasswordInvalidException()).when(userLoginRemoteService).login(any(), any());

        // When && Then
        given()
                .formParams("username", "username",
                        "password", "password",
                        "email", "email")
                .when()
                .post("/users/login")
                .then()
                .statusCode(401)
                .contentType("text/plain")
                .body(Matchers.equalTo("Username or password invalid"));
        verify(userLoginRemoteService, times(1)).login(any(), any());
        verifyNoMoreInteractions(userLoginRemoteService);
    }

    @Test
    public void should_get_anonymous_user() {
        given()
                .get("/users/me")
                .then()
                .log().all()
                .statusCode(200)
                .body("anonymous", equalTo(Boolean.TRUE));
    }

    @ParameterizedTest
    @ValueSource(strings = {"damdamdeo/damdamdeo", "roger/roger"})
    public void should_get_authenticated_user(final String usernamePassorwd) {
        // Given
        final String username = usernamePassorwd.split("/")[0];
        final String password = usernamePassorwd.split("/")[1];
        final String accessToken = given()
                .formParams("username", username,
                        "password", password)
                .when()
                .post("/users/login")
                .then()
                .statusCode(200)
                .extract()
                .path("accessToken");

        // When && Then
        given()
                .header("Authorization", "Bearer " + accessToken)
                .when().get("/users/me")
                .then()
                .log().all()
                .statusCode(200)
                .body("anonymous", equalTo(Boolean.FALSE))
                .body("roles", containsInAnyOrder("frontend-user"))
                .body("userName", equalTo(username));
    }

}
