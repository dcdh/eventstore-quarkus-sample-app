package com.damdamdeo.todo.domain;

import com.damdamdeo.eventsourcing.domain.Payload;
import com.damdamdeo.todo.domain.event.PayloadAdapter;
import com.damdamdeo.todo.domain.event.TodoCreatedEventPayload;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TodoCreatedEventPayloadTest {

    private static final Jsonb MAPPER = JsonbBuilder.create(new JsonbConfig().withFormatting(true)
            .withAdapters(new PayloadAdapter())
    );

    @Test
    public void should_serialize() throws JSONException {
        // Given
        final Payload eventPayload = new TodoCreatedEventPayload("todoId", "lorem ipsum");

        // When
        final String json = MAPPER.toJson(eventPayload);

        // Then
        JSONAssert.assertEquals(
                "{\"@class\": \"TodoCreatedEventPayload\", \"todoId\":\"todoId\", \"description\":\"lorem ipsum\"}", json, JSONCompareMode.STRICT);
    }

    @Test
    public void should_deserialize() {
        // Given
        final String json = "{\"@class\": \"TodoCreatedEventPayload\", \"todoId\":\"todoId\", \"description\":\"lorem ipsum\"}";

        // When
        final TodoCreatedEventPayload payload = (TodoCreatedEventPayload) MAPPER.fromJson(json, Payload.class);

        // Then
        assertEquals("todoId", payload.todoId());
        assertEquals("lorem ipsum", payload.description());
    }

}
