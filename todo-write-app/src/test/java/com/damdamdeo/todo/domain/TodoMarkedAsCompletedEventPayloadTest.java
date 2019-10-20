package com.damdamdeo.todo.domain;

import com.damdamdeo.todo.aggregate.event.TodoMarkedAsCompletedEventPayload;
import com.damdamdeo.todo.user.type.DefaultEventPayloadsAdapter;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TodoMarkedAsCompletedEventPayloadTest {

    private static final Jsonb MAPPER = JsonbBuilder.create(new JsonbConfig()
            .withFormatting(true)
            .withAdapters(new DefaultEventPayloadsAdapter()));

    @Test
    public void should_serialize() throws JSONException {
        // Given
        final TodoMarkedAsCompletedEventPayload todoMarkedAsCompletedEventPayload = new TodoMarkedAsCompletedEventPayload("todoId");

        // When
        final String json = MAPPER.toJson(todoMarkedAsCompletedEventPayload);

        // Then
        JSONAssert.assertEquals(
                "{\"@payloadType\": \"TodoMarkedAsCompletedEventPayload\", \"@aggregaterootType\": \"TodoAggregateRoot\", \"todoId\":\"todoId\"}", json, JSONCompareMode.STRICT);
    }

    @Test
    public void should_deserialize() {
        // Given
        final String json = "{\"@payloadType\": \"TodoMarkedAsCompletedEventPayload\", \"@aggregaterootType\": \"TodoAggregateRoot\", \"todoId\":\"todoId\"}";

        // When
        final TodoMarkedAsCompletedEventPayload todoMarkedAsCompletedEventPayload = MAPPER.fromJson(json, TodoMarkedAsCompletedEventPayload.class);

        // Then
        assertEquals("todoId", todoMarkedAsCompletedEventPayload.todoId());
    }

}
