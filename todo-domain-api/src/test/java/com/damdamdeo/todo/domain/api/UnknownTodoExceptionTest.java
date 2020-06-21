package com.damdamdeo.todo.domain.api;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class UnknownTodoExceptionTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(UnknownTodoException.class).verify();
    }

}
