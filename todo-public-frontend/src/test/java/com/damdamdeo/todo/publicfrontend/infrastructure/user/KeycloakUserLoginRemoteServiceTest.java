package com.damdamdeo.todo.publicfrontend.infrastructure.user;

import com.damdamdeo.todo.publicfrontend.domain.user.AccessToken;
import com.damdamdeo.todo.publicfrontend.domain.user.UsernameOrPasswordInvalidException;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
public class KeycloakUserLoginRemoteServiceTest {

    @Inject
    KeycloakUserLoginRemoteService keycloakUserLoginRemoteService;

    @Test
    public void should_login() throws Exception {
        // Given

        // When
        final AccessToken accessToken = keycloakUserLoginRemoteService.login("damdamdeo", "damdamdeo");

        // Then
        assertNotNull(accessToken.accessToken());
    }

    @Test
    public void should_throw_username_or_password_invalid_exception_when_using_wrong_login_username() {
        // Given

        // When && Then
        assertThrows(UsernameOrPasswordInvalidException.class, () -> keycloakUserLoginRemoteService.login("wrongUsername", "damdamdeo"));
    }

    @Test
    public void should_throw_username_or_password_invalid_exception_when_using_wrong_login_password() {
        // Given

        // When && Then
        assertThrows(UsernameOrPasswordInvalidException.class, () -> keycloakUserLoginRemoteService.login("damdamdeo", "wrongPassword"));
    }

}
