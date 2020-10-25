package com.damdamdeo.todo.infrastructure.deserializer;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.ApiAggregateRootId;
import com.damdamdeo.todo.domain.event.TodoMarkedAsCompletedEventPayload;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class TodoMarkedAsCompletedEventPayloadTest {

    @Inject
    TodoMarkedAsCompletedEventPayloadDeSerializer todoMarkedAsCompletedEventPayloadDeSerializer;

    @Test
    public void should_serialize() throws Exception {
        // Given

        // When
        final JsonObject serialized = todoMarkedAsCompletedEventPayloadDeSerializer.encode(
                new ApiAggregateRootId("todoId", "TodoAggregateRoot"),
                new TodoMarkedAsCompletedEventPayload("todoId"));

        // Then
        final String expectedJsonPayload = new Scanner(this.getClass().getResourceAsStream("/expected/todoMarkedAsCompletedEventPayload.json"), "UTF-8")
                .useDelimiter("\\A").next();
        JSONAssert.assertEquals(expectedJsonPayload, serialized.toString(), true);
    }

    @Test
    public void should_deserialize() throws Exception {
        // Given
        final JsonObject jsonObject = Json.createReader(new StringReader("{\"todoId\": \"todoId\"}")).readObject();

        // When
        final TodoMarkedAsCompletedEventPayload deserialized = (TodoMarkedAsCompletedEventPayload) todoMarkedAsCompletedEventPayloadDeSerializer.decode(jsonObject);

        // Then
        assertEquals(new TodoMarkedAsCompletedEventPayload("todoId"), deserialized);
    }

}
