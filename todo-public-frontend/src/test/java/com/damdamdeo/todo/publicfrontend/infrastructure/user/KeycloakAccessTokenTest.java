package com.damdamdeo.todo.publicfrontend.infrastructure.user;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class KeycloakAccessTokenTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(KeycloakAccessToken.class).verify();
    }

}
