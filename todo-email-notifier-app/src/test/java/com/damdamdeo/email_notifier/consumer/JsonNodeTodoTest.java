package com.damdamdeo.email_notifier.consumer;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class JsonNodeTodoTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(JsonNodeTodo.class).verify();
    }

}
