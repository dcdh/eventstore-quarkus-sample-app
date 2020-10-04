package com.damdamdeo.todo.domain.event;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class TodoMarkedAsCompletedEventPayloadTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(TodoMarkedAsCompletedEventPayload.class).verify();
    }

}
