package com.damdamdeo.todo.aggregate.event;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.ApiAggregateRootId;
import com.damdamdeo.todo.infrastructure.TodoMarkedAsCompletedEventPayloadDeSerializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.inject.Inject;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class TodoMarkedAsCompletedEventPayloadTest {

    @Inject
    TodoMarkedAsCompletedEventPayloadDeSerializer todoMarkedAsCompletedEventPayloadDeSerializer;

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(TodoMarkedAsCompletedEventPayload.class).verify();
    }

    @Test
    public void should_serialize() throws Exception {
        // Given
        final ObjectMapper objectMapper = new ObjectMapper();

        // When
        final JsonNode serialized = todoMarkedAsCompletedEventPayloadDeSerializer.encode(
                new ApiAggregateRootId("todoId", "TodoAggregateRoot"),
                new TodoMarkedAsCompletedEventPayload("todoId"), objectMapper);

        // Then
        final String expectedJsonPayload = new Scanner(this.getClass().getResourceAsStream("/expected/todoMarkedAsCompletedEventPayload.json"), "UTF-8")
                .useDelimiter("\\A").next();
        JSONAssert.assertEquals(expectedJsonPayload, serialized.toString(), true);
    }

    @Test
    public void should_deserialize() throws Exception {
        // Given
        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode jsonNode = objectMapper.readTree("{\"todoId\": \"todoId\"}");

        // When
        final TodoMarkedAsCompletedEventPayload deserialized = (TodoMarkedAsCompletedEventPayload) todoMarkedAsCompletedEventPayloadDeSerializer.decode(jsonNode);

        // Then
        assertEquals(new TodoMarkedAsCompletedEventPayload("todoId"), deserialized);
    }

}
