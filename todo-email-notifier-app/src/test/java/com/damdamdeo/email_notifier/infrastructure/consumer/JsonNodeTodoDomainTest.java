package com.damdamdeo.email_notifier.infrastructure.consumer;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class JsonNodeTodoDomainTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(JsonNodeTodo.class).verify();
    }

}
