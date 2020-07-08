package com.damdamdeo.todo.publicfrontend.infrastructure.user;

import com.damdamdeo.todo.publicfrontend.domain.user.AccessToken;
import com.damdamdeo.todo.publicfrontend.domain.user.UsernameOrPasswordInvalidException;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@QuarkusTest
public class KeycloakUserLoginRemoteServiceTest {

    @Inject
    KeycloakUserLoginRemoteService keycloakUserLoginRemoteService;

    @ParameterizedTest
    @ValueSource(strings = {"damdamdeo/damdamdeo", "roger/roger"})
    public void should_login(final String usernamePassorwd) throws Exception {
        // Given
        final String username = usernamePassorwd.split("/")[0];
        final String password = usernamePassorwd.split("/")[1];

        // When
        final AccessToken accessToken = keycloakUserLoginRemoteService.login(username, password);

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
