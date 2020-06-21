package com.damdamdeo.email_notifier.consumer;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class DefaultTodoMarkedAsCompletedTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(DefaultTodoMarkedAsCompleted.class).verify();
    }

}
