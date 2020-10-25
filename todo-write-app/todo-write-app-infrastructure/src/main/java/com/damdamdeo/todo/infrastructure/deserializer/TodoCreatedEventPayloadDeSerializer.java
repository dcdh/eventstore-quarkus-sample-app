package com.damdamdeo.todo.infrastructure.deserializer;

import com.damdamdeo.eventsourced.encryption.api.JsonbCryptoService;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRootEventPayload;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.JsonbAggregateRootEventPayloadDeSerializer;
import com.damdamdeo.todo.domain.event.TodoCreatedEventPayload;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import java.util.Objects;

@ApplicationScoped
public class TodoCreatedEventPayloadDeSerializer implements JsonbAggregateRootEventPayloadDeSerializer {

    final JsonbCryptoService jsonbCryptoService;

    public TodoCreatedEventPayloadDeSerializer(final JsonbCryptoService jsonbCryptoService) {
        this.jsonbCryptoService = Objects.requireNonNull(jsonbCryptoService);
    }

    @Override
    public String aggregateRootType() {
        return "TodoAggregateRoot";
    }

    @Override
    public String eventType() {
        return "TodoCreatedEvent";
    }

    @Override
    public JsonObject encode(final AggregateRootId aggregateRootId, final AggregateRootEventPayload aggregateRootEventPayload) {
        final TodoCreatedEventPayload todoCreatedEventPayload = (TodoCreatedEventPayload) aggregateRootEventPayload;
        return Json.createObjectBuilder()
                .add("todoId", todoCreatedEventPayload.todoId())
                .add("description", jsonbCryptoService.encrypt(aggregateRootId, todoCreatedEventPayload.description(), true))
                .build();
    }

    @Override
    public AggregateRootEventPayload decode(final JsonObject json) {
        return new TodoCreatedEventPayload(
                json.getString("todoId"),
                json.getString("description")
        );
    }

}
