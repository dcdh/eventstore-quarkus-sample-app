package com.damdamdeo.todo.infrastructure;

import com.damdamdeo.eventsourced.encryption.infra.jackson.JsonCryptoService;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.JacksonAggregateRootMaterializedStateSerializer;
import com.damdamdeo.todo.aggregate.TodoAggregateRoot;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class TodoAggregateJacksonAggregateRootMaterializedStateSerializer implements JacksonAggregateRootMaterializedStateSerializer {

    final JsonCryptoService jsonCryptoService;

    public TodoAggregateJacksonAggregateRootMaterializedStateSerializer(final JsonCryptoService jsonCryptoService) {
        this.jsonCryptoService = Objects.requireNonNull(jsonCryptoService);
    }

    @Override
    public String aggregateRootType() {
        return "TodoAggregateRoot";
    }

    @Override
    public JsonNode encode(final AggregateRoot aggregateRoot,
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

}
