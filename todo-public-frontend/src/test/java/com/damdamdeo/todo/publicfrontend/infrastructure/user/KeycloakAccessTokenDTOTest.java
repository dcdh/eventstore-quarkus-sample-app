package com.damdamdeo.todo.publicfrontend.infrastructure.user;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class KeycloakAccessTokenDTOTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(KeycloakAccessTokenDTO.class).verify();
    }

}
