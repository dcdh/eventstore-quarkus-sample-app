package com.damdamdeo.todo.publicfrontend.interfaces;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class TodoDTOTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(TodoDTO.class).verify();
    }

}
