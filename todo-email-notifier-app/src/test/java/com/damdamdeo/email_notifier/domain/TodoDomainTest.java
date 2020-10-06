package com.damdamdeo.email_notifier.domain;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

public class TodoDomainTest {

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(TodoDomain.class).verify();
    }

}
