package com.damdamdeo.todo.domain.event;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class TodoCreatedEventPayloadTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(TodoCreatedEventPayload.class).verify();
    }

}
