package com.damdamdeo.todo.domain;

import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MetaDataTest {

    private static final Jsonb MAPPER = JsonbBuilder.create(new JsonbConfig().withFormatting(true));

    @Test
    public void should_serialize() throws JSONException {
        // Given
        final Map<String, Object> metaData = new HashMap<>();
        metaData.put("user", "damien");

        // When
        final String json = MAPPER.toJson(metaData);

        // Then
        JSONAssert.assertEquals(
                "{\"user\": \"damien\"}", json, JSONCompareMode.STRICT);
    }

    @Test
    public void should_deserialize() {
        // Given
        final String json = "{\"user\": \"damien\"}";

        // When
        final Map<String, Object> metaData = MAPPER.fromJson(json, Map.class);

        // Then
        assertEquals(metaData.get("user"), "damien");
    }

}
