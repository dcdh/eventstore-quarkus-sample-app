package com.damdamdeo.todo.domain;

import com.damdamdeo.todo.aggregate.event.TodoCreatedEventPayload;
import com.damdamdeo.todo.user.type.DefaultEventPayloadsAdapter;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TodoCreatedEventPayloadTest {

    private static final Jsonb MAPPER = JsonbBuilder.create(new JsonbConfig()
            .withFormatting(true)
            .withAdapters(new DefaultEventPayloadsAdapter()));

    @Test
    public void should_serialize() throws JSONException {
        // Given
        final TodoCreatedEventPayload todoCreatedEventPayload = new TodoCreatedEventPayload("todoId", "lorem ipsum");

        // When
        final String json = MAPPER.toJson(todoCreatedEventPayload);

        // Then
        JSONAssert.assertEquals(
                "{\"@payloadType\": \"TodoCreatedEventPayload\", \"@aggregaterootType\": \"TodoAggregateRoot\", \"todoId\":\"todoId\", \"description\":\"lorem ipsum\"}", json, JSONCompareMode.STRICT);
    }

    @Test
    public void should_deserialize() {
        // Given
        final String json = "{\"@payloadType\": \"TodoCreatedEventPayload\", \"@aggregaterootType\": \"TodoAggregateRoot\", \"todoId\":\"todoId\", \"description\":\"lorem ipsum\"}";

        // When
        final TodoCreatedEventPayload todoCreatedEventPayload = MAPPER.fromJson(json, TodoCreatedEventPayload.class);

        // Then
        assertEquals("todoId", todoCreatedEventPayload.todoId());
        assertEquals("lorem ipsum", todoCreatedEventPayload.description());
    }

}
