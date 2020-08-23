package com.damdamdeo.todo.infrastructure;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRootEventPayload;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.JacksonAggregateRootEventPayloadDeSerializer;
import com.damdamdeo.todo.aggregate.event.TodoMarkedAsCompletedEventPayload;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TodoMarkedAsCompletedEventPayloadDeSerializer implements JacksonAggregateRootEventPayloadDeSerializer {

    @Override
    public String aggregateRootType() {
        return "TodoAggregateRoot";
    }

    @Override
    public String eventType() {
        return "TodoMarkedAsCompletedEvent";
    }

    @Override
    public JsonNode encode(final AggregateRootId aggregateRootId,
                           final AggregateRootEventPayload aggregateRootEventPayload,
                           final ObjectMapper objectMapper) {
        final TodoMarkedAsCompletedEventPayload todoMarkedAsCompletedEventPayload = (TodoMarkedAsCompletedEventPayload) aggregateRootEventPayload;
        final ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("todoId", todoMarkedAsCompletedEventPayload.todoId());
        return objectNode;
    }

    @Override
    public AggregateRootEventPayload decode(final JsonNode json) {
        return new TodoMarkedAsCompletedEventPayload(
                json.get("todoId").asText()
        );
    }

}
