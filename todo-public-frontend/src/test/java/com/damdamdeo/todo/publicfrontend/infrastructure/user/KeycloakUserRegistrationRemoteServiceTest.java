package com.damdamdeo.todo.publicfrontend.infrastructure.user;

import com.damdamdeo.todo.publicfrontend.domain.user.UsernameOrEmailAlreadyUsedException;
import com.damdamdeo.todo.publicfrontend.infrastructure.user.KeycloakUserRegistrationRemoteService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.apache.commons.lang3.Validate;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;

import javax.inject.Inject;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
public class KeycloakUserRegistrationRemoteServiceTest {

    @Inject
    KeycloakUserRegistrationRemoteService keycloakUserAuthenticationRemoteService;

    @ConfigProperty(name = "quarkus.oidc.auth-server-url")
    String keyCloakServerAuthUrl;

    @BeforeEach
    @AfterEach
    public void flush() {
        // flush all tests users excepted damdamdeo and roger whom are not considered as test users
        final Pattern pattern = Pattern.compile("^(.*)\\/realms\\/(.*)$");
        final Matcher matcher = pattern.matcher(keyCloakServerAuthUrl);
        Validate.validState(matcher.matches());
        final String serverUrl = matcher.group(1);
        final Keycloak keycloak = Keycloak.getInstance(serverUrl, "master", "keycloak", "keycloak", "admin-cli");
        final String userRealm = matcher.group(2);
        keycloak.realm(userRealm).users()
                .list().stream()
                .filter(userRepresentation -> !"damdamdeo".equals(userRepresentation.getUsername()))
                .filter(userRepresentation -> !"roger".equals(userRepresentation.getUsername()))
                .forEach(userToDelete -> keycloak.realm(userRealm).users().delete(userToDelete.getId()));
    }

    @Test
    public void should_register_new_user() throws Exception {
        // Given
        final String generatedUser = UUID.randomUUID().toString();
        final String password = "password";
        final String email = String.format("%s@test", generatedUser);

        // When
        keycloakUserAuthenticationRemoteService.register(generatedUser, password, email);

        // Then
        RestAssured
                .given()
                .param("grant_type", "password")
                .param("username", generatedUser)
                .param("password", password)
                .param("client_id", "todo-public-frontend-service")
                .param("client_secret", "secret")
                .when()
                .post(keyCloakServerAuthUrl + "/protocol/openid-connect/token")
                .then()
                .statusCode(200);
    }

    @Test
    public void should_throw_exception_when_username_is_already_used() throws Exception {
        // Given
        final String generatedUser = UUID.randomUUID().toString();
        final String password = "password";

        keycloakUserAuthenticationRemoteService.register(generatedUser, password, String.format("%s@test1", generatedUser));

        // When && Then
        final UsernameOrEmailAlreadyUsedException usernameOrEmailAlreadyUsedException = assertThrows(UsernameOrEmailAlreadyUsedException.class, () -> {
            keycloakUserAuthenticationRemoteService.register(generatedUser, password, String.format("%s@test2", generatedUser));
        });
        assertEquals(generatedUser, usernameOrEmailAlreadyUsedException.username());
        assertEquals(String.format("%s@test2", generatedUser), usernameOrEmailAlreadyUsedException.email());
    }

    @Test
    public void should_throw_exception_when_email_is_already_used() throws Exception {
        // Given
        final String password = "password";
        final String sameEmail = String.format("%s@test", UUID.randomUUID().toString());
        final String secondUsername = UUID.randomUUID().toString();
        keycloakUserAuthenticationRemoteService.register(UUID.randomUUID().toString(), password, sameEmail);

        // When && Then
        final UsernameOrEmailAlreadyUsedException usernameOrEmailAlreadyUsedException = assertThrows(UsernameOrEmailAlreadyUsedException.class, () -> {
            keycloakUserAuthenticationRemoteService.register(secondUsername, password, sameEmail);
        });
        assertEquals(secondUsername, usernameOrEmailAlreadyUsedException.username());
        assertEquals(sameEmail, usernameOrEmailAlreadyUsedException.email());
    }

}
