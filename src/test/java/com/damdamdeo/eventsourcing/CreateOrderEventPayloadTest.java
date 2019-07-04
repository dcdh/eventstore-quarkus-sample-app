package com.damdamdeo.eventsourcing;

import com.damdamdeo.eventsourcing.domain.Payload;
import com.damdamdeo.order.domain.event.CreateOrderEventPayload;
import com.damdamdeo.order.domain.event.PayloadAdapter;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateOrderEventPayloadTest {

    private static final Jsonb MAPPER = JsonbBuilder.create(new JsonbConfig().withFormatting(true)
            .withAdapters(new PayloadAdapter())
    );

    @Test
    public void should_serialize() throws JSONException {
        // Given
        final Payload eventPayload = new CreateOrderEventPayload("orderId", "articleName", 10l);

        // When
        final String json = MAPPER.toJson(eventPayload);

        // Then
        JSONAssert.assertEquals(
                "{\"@class\": \"CreateOrderEventPayload\", \"articleName\":\"articleName\", \"orderId\":\"orderId\", \"quantity\":10}", json, JSONCompareMode.STRICT);
    }

    @Test
    public void should_deserialize() {
        // Given
        final String json = "{\"@class\": \"CreateOrderEventPayload\", \"articleName\":\"articleName\", \"orderId\":\"orderId\", \"quantity\":10}";

        // When
        final CreateOrderEventPayload payload = (CreateOrderEventPayload) MAPPER.fromJson(json, Payload.class);

        // Then
        assertEquals(payload.articleName(), payload.articleName());
        assertEquals(payload.orderId(), payload.orderId());
        assertEquals(payload.quantity(), payload.quantity());
    }

}
