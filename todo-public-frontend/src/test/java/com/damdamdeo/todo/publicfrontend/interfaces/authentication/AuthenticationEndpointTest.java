package com.damdamdeo.todo.publicfrontend.interfaces.authentication;

import com.damdamdeo.todo.publicfrontend.domain.authentication.UserAuthenticationRemoteService;
import com.damdamdeo.todo.publicfrontend.domain.authentication.UsernameOrPasswordInvalidException;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;
import io.restassured.http.ContentType;
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
    UserAuthenticationRemoteService userAuthenticationRemoteService;

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
                .post("/authentication/login")
                .then()
                .statusCode(200)
                .body("accessToken", notNullValue())
                .body("expiresIn", notNullValue())
                .body("refreshToken", notNullValue())
                .body("refreshExpiresIn", notNullValue());
        verify(userAuthenticationRemoteService, times(1)).login(username, password);
        verifyNoMoreInteractions(userAuthenticationRemoteService);
    }

    @Test
    public void should_akveo_login() {
        // Given
        final String username = "damdamdeo";
        final String password = "damdamdeo";

        // When && Then
        given()
                .formParams("username", username,
                        "password", password)
                .when()
                .post("/authentication/akveo/login")
                .then()
                .statusCode(200)
                .body("token.access_token", notNullValue())
                .body("token.expires_in", notNullValue())
                .body("token.refresh_token", notNullValue());
    }

    @Test
    public void should_return_expected_response_when_logging_using_a_wrong_username_or_password() throws Exception {
        // Given
        doThrow(new UsernameOrPasswordInvalidException()).when(userAuthenticationRemoteService).login(any(), any());

        // When && Then
        given()
                .formParams("username", "username",
                        "password", "password",
                        "email", "email")
                .when()
                .post("/authentication/login")
                .then()
                .statusCode(401)
                .contentType("text/plain")
                .body(Matchers.equalTo("Username or password invalid"));
        verify(userAuthenticationRemoteService, times(1)).login(any(), any());
        verifyNoMoreInteractions(userAuthenticationRemoteService);
    }

    @Test
    public void should_get_anonymous_user() {
        given()
                .get("/authentication/me")
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
                .post("/authentication/login")
                .then()
                .statusCode(200)
                .extract()
                .path("accessToken");

        // When && Then
        given()
                .header("Authorization", "Bearer " + accessToken)
                .when().get("/authentication/me")
                .then()
                .log().all()
                .statusCode(200)
                .body("anonymous", equalTo(Boolean.FALSE))
                .body("roles", containsInAnyOrder("frontend-user"))
                .body("userName", equalTo(username));
    }

    @Test
    public void should_refresh_token() {
        // Given
        final String refreshToken = given()
                .formParams("username", "damdamdeo",
                        "password", "damdamdeo")
                .when()
                .post("/authentication/login")
                .then()
                .statusCode(200)
                .extract()
                .path("refreshToken");

        // When && Then
        given()
                .formParams("refreshToken", refreshToken)
                .when().post("/authentication/refresh-token")
                .then()
                .log().all()
                .statusCode(200)
                .body("accessToken", notNullValue())
                .body("expiresIn", notNullValue())
                .body("refreshToken", notNullValue())
                .body("refreshExpiresIn", notNullValue());
    }

    @Test
    public void should_akveo_refresh_token() {
        // Given
        final String refreshToken = given()
                .formParams("username", "damdamdeo",
                        "password", "damdamdeo")
                .when()
                .post("/authentication/login")
                .then()
                .statusCode(200)
                .extract()
                .path("refreshToken");

        // When && Then
        given()
                .contentType(ContentType.JSON)
                .body(String.format("{\"token\":{\"refresh_token\":\"%s\", \"access_token\":\"accessToken\"}}", refreshToken))
                .when().post("/authentication/akveo/refresh-token")
                .then()
                .log().all()
                .statusCode(200)
                .body("token.access_token", notNullValue())
                .body("token.expires_in", notNullValue())
                .body("token.refresh_token", notNullValue());
    }

    @Test
    public void should_return_expected_response_when_refreshing_token_using_an_invalid_refresh_token() throws Exception {
        given()
                .formParams("refreshToken", "invalidRefreshToken")
                .when().post("/authentication/refresh-token")
                .then()
                .log().all()
                .statusCode(400)
                .contentType("text/plain")
                .body(Matchers.equalTo("Refresh token invalid"));
    }

}
