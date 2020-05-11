package com.damdamdeo.todo.publicfrontend.interfaces;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class UserDTOTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(AuthenticationEndpoint.UserDTO.class).verify();
    }

}
