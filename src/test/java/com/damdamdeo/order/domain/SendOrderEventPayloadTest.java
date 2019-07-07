package com.damdamdeo.order.domain;

import com.damdamdeo.eventsourcing.domain.Payload;
import com.damdamdeo.order.domain.event.PayloadAdapter;
import com.damdamdeo.order.domain.event.SendOrderEventPayload;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SendOrderEventPayloadTest {

    private static final Jsonb MAPPER = JsonbBuilder.create(new JsonbConfig().withFormatting(true)
            .withAdapters(new PayloadAdapter())
    );

    @Test
    public void should_serialize() throws JSONException {
        // Given
        final Payload eventPayload = new SendOrderEventPayload("orderId");

        // When
        final String json = MAPPER.toJson(eventPayload);

        // Then
        JSONAssert.assertEquals(
                "{\"@class\": \"SendOrderEventPayload\", \"orderId\":\"orderId\"}", json, JSONCompareMode.STRICT);
    }

    @Test
    public void should_deserialize() {
        // Given
        final String json = "{\"@class\": \"SendOrderEventPayload\", \"orderId\":\"orderId\"}";

        // When
        final SendOrderEventPayload payload = (SendOrderEventPayload) MAPPER.fromJson(json, Payload.class);

        // Then
        assertEquals(payload.orderId(), payload.orderId());
    }

}
