package com.damdamdeo.todo.domain.usecase;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class CreateTodoCommandTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(CreateTodoCommand.class).verify();
    }

}
