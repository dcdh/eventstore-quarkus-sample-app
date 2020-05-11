package com.damdamdeo.todo.publicfrontend;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.keycloak.representations.AccessTokenResponse;

import static io.restassured.RestAssured.given;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.IsEqual.equalTo;

@QuarkusTest
public class AuthenticationEndpointTest {

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String keyCloakServerAuthUrl;

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
        final String username = usernamePassorwd.split("/")[0];
        final String password = usernamePassorwd.split("/")[1];
        RestAssured.given()
                .auth().oauth2(getAccessToken(username, password))
                .when().get("/users/me")
                .then()
                .log().all()
                .statusCode(200)
                .body("anonymous", equalTo(Boolean.FALSE))
                .body("roles", containsInAnyOrder("frontend-user"))
                .body("userName", equalTo(username));
    }

    private String getAccessToken(final String userName, final String password) {
        return RestAssured
                .given()
                .param("grant_type", "password")
                .param("username", userName)
                .param("password", password)
                .param("client_id", "todo-public-frontend-service")
                .param("client_secret", "secret")
                .when()
                .post(keyCloakServerAuthUrl + "/protocol/openid-connect/token")
                .as(AccessTokenResponse.class).getToken();
    }

}
