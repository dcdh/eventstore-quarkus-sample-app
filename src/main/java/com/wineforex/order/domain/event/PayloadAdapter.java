package com.wineforex.order.domain.event;

import com.damdamdeo.eventsourcing.domain.Payload;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.adapter.JsonbAdapter;

public class PayloadAdapter implements JsonbAdapter<Payload, JsonObject> {

    private final static String DISCRIMINATOR = "@class";

    @Override
    public JsonObject adaptToJson(Payload payload) throws Exception {
        final String payloadTypeSimpleName = payload.getClass().getSimpleName();
        switch (payloadTypeSimpleName) {
            case "CreateOrderEventPayload":
                return Json.createObjectBuilder()
                        .add(DISCRIMINATOR, payloadTypeSimpleName)
                        .add("orderId", ((CreateOrderEventPayload) payload).orderId())
                        .add("articleName", ((CreateOrderEventPayload) payload).articleName())
                        .add("quantity", ((CreateOrderEventPayload) payload).quantity())
                        .build();
            default:
                throw new IllegalStateException("Unknown type : " + payloadTypeSimpleName);
        }
    }

    @Override
    public Payload adaptFromJson(JsonObject payload) throws Exception {
        switch (payload.getString(DISCRIMINATOR)) {
            case "CreateOrderEventPayload":
                return new CreateOrderEventPayload(payload.getString("orderId"),
                        payload.getString("articleName"),
                        payload.getJsonNumber("quantity").longValue());
            default:
                throw new IllegalStateException("Unknown type : " + payload.getString(DISCRIMINATOR));
        }
    }

}
