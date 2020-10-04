package com.damdamdeo.todo.domain.command;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class MarkTodoAsCompletedCommandTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(MarkTodoAsCompletedCommand.class).verify();
    }

}
