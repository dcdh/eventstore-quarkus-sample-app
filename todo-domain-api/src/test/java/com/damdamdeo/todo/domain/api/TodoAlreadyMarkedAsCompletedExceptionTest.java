package com.damdamdeo.todo.domain.api;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class TodoAlreadyMarkedAsCompletedExceptionTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(TodoAlreadyMarkedAsCompletedException.class)
                .withIgnoredFields("detailMessage", "cause", "stackTrace", "suppressedExceptions")
                .verify();
    }

}
