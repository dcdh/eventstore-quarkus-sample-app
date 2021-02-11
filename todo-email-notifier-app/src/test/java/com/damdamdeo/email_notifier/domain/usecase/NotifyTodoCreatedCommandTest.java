package com.damdamdeo.email_notifier.domain.usecase;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class NotifyTodoCreatedCommandTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(NotifyTodoCreatedCommand.class)
                .verify();
    }

}
