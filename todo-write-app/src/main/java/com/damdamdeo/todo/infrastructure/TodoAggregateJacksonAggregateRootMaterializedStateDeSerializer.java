package com.damdamdeo.todo.infrastructure;

import com.damdamdeo.eventsourced.encryption.infra.jackson.JsonCryptoService;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.JacksonAggregateRootMaterializedStateDeSerializer;
import com.damdamdeo.todo.aggregate.TodoAggregateRoot;
import com.damdamdeo.todo.domain.api.TodoStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class TodoAggregateJacksonAggregateRootMaterializedStateDeSerializer implements JacksonAggregateRootMaterializedStateDeSerializer {

    final JsonCryptoService jsonCryptoService;

    public TodoAggregateJacksonAggregateRootMaterializedStateDeSerializer(final JsonCryptoService jsonCryptoService) {
        this.jsonCryptoService = Objects.requireNonNull(jsonCryptoService);
    }

    @Override
    public String aggregateRootType() {
        return "TodoAggregateRoot";
    }

    @Override
    public JsonNode serialize(final AggregateRoot aggregateRoot,
                              final boolean shouldEncrypt,
                              final ObjectMapper objectMapper) {
        final TodoAggregateRoot todoAggregateRoot = (TodoAggregateRoot) aggregateRoot;
        final ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put("description", todoAggregateRoot.description());
        objectNode.put("todoId", todoAggregateRoot.todoId());
        objectNode.put("todoStatus", todoAggregateRoot.todoStatus().name());
        jsonCryptoService.encrypt(aggregateRoot.aggregateRootId(), objectNode, "description", shouldEncrypt);
        return objectNode;
    }

    @Override
    public TodoAggregateRoot deserialize(final AggregateRootId aggregateRootId, final JsonNode aggregateRoot, final Long version) {
        return TodoAggregateRoot.newBuilder()
                .withAggregateRootId(aggregateRootId.aggregateRootId())
                .withVersion(version)
                .withDescription(aggregateRoot.get("description").asText())
                .withTodoStatus(TodoStatus.valueOf(aggregateRoot.get("todoStatus").asText()))
                .build();
    }

}
