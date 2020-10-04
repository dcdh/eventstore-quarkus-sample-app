package com.damdamdeo.todo.infrastructure.deserializer;

import com.damdamdeo.eventsourced.encryption.infra.jackson.JsonCryptoService;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRootEventPayload;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.JacksonAggregateRootEventPayloadDeSerializer;
import com.damdamdeo.todo.domain.event.TodoCreatedEventPayload;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class TodoCreatedEventPayloadDeSerializer implements JacksonAggregateRootEventPayloadDeSerializer {

    final JsonCryptoService jsonCryptoService;

    public TodoCreatedEventPayloadDeSerializer(final JsonCryptoService jsonCryptoService) {
        this.jsonCryptoService = Objects.requireNonNull(jsonCryptoService);
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
    public JsonNode encode(final AggregateRootId aggregateRootId,
                           final AggregateRootEventPayload aggregateRootEventPayload,
                           final ObjectMapper objectMapper) {
        final TodoCreatedEventPayload todoCreatedEventPayload = (TodoCreatedEventPayload) aggregateRootEventPayload;
        final ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("todoId", todoCreatedEventPayload.todoId());
        objectNode.put("description", todoCreatedEventPayload.description());
        jsonCryptoService.encrypt(aggregateRootId, objectNode, "description", true);
        return objectNode;
    }

    @Override
    public AggregateRootEventPayload decode(final JsonNode json) {
        return new TodoCreatedEventPayload(
                json.get("todoId").asText(),
                json.get("description").asText()
        );
    }

}
