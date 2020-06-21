package com.damdamdeo.todo.infrastructure;

import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class UUIDTodoIdGeneratorTest {

    private static final Pattern uuidPattern = Pattern.compile("([a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8})");

    @Test
    public void should_generate_UUID() {
        // Given
        final UUIDTodoIdGenerator uuidTodoIdGenerator = new UUIDTodoIdGenerator();

        // When
        final String todoIdGenerated = uuidTodoIdGenerator.generateTodoId();

        // Then
        assertTrue(uuidPattern.matcher(todoIdGenerated).matches());
    }

}
