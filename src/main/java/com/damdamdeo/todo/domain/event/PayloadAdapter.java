package com.damdamdeo.todo.domain.event;

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
            case "TodoCreatedEventPayload":
                return Json.createObjectBuilder()
                        .add(DISCRIMINATOR, payloadTypeSimpleName)
                        .add("todoId", ((TodoCreatedEventPayload) payload).todoId())
                        .add("description", ((TodoCreatedEventPayload) payload).description())
                        .build();
            case "TodoMarkedAsCompletedEventPayload":
                return Json.createObjectBuilder()
                        .add(DISCRIMINATOR, payloadTypeSimpleName)
                        .add("todoId", ((TodoMarkedAsCompletedEventPayload) payload).todoId())
                        .build();
            default:
                throw new IllegalStateException("Unknown type : " + payloadTypeSimpleName);
        }
    }

    @Override
    public Payload adaptFromJson(JsonObject payload) throws Exception {
        switch (payload.getString(DISCRIMINATOR)) {
            case "TodoCreatedEventPayload":
                return new TodoCreatedEventPayload(payload.getString("todoId"),
                        payload.getString("description"));
            case "TodoMarkedAsCompletedEventPayload":
                return new TodoMarkedAsCompletedEventPayload(payload.getString("todoId"));
            default:
                throw new IllegalStateException("Unknown type : " + payload.getString(DISCRIMINATOR));
        }
    }

}
