package com.damdamdeo.todo.domain.command;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class CreateNewTodoCommandTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(CreateNewTodoCommand.class).verify();
    }

}
