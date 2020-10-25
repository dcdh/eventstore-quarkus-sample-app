package com.damdamdeo.todo.infrastructure.deserializer;

import com.damdamdeo.eventsourced.encryption.api.JsonbCryptoService;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRoot;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.JsonbAggregateRootMaterializedStateDeSerializer;
import com.damdamdeo.todo.domain.TodoAggregateRoot;
import com.damdamdeo.todo.domain.api.TodoStatus;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import java.util.Objects;

@ApplicationScoped
public class TodoAggregateJacksonAggregateRootMaterializedStateDeSerializer implements JsonbAggregateRootMaterializedStateDeSerializer {

    final JsonbCryptoService jsonbCryptoService;

    public TodoAggregateJacksonAggregateRootMaterializedStateDeSerializer(final JsonbCryptoService jsonbCryptoService) {
        this.jsonbCryptoService = Objects.requireNonNull(jsonbCryptoService);
    }

    @Override
    public String aggregateRootType() {
        return "TodoAggregateRoot";
    }

    @Override
    public JsonObject serialize(final AggregateRoot aggregateRoot, final boolean shouldEncrypt) {
        final TodoAggregateRoot todoAggregateRoot = (TodoAggregateRoot) aggregateRoot;
        return Json.createObjectBuilder()
                .add("description", jsonbCryptoService.encrypt(aggregateRoot.aggregateRootId(), todoAggregateRoot.description(), shouldEncrypt))
                .add("todoId", todoAggregateRoot.todoId())
                .add("todoStatus", todoAggregateRoot.todoStatus().name())
                .build();
    }

    @Override
    public TodoAggregateRoot deserialize(final AggregateRootId aggregateRootId, final JsonObject aggregateRoot, final Long version) {
        return TodoAggregateRoot.newBuilder()
                .withAggregateRootId(aggregateRootId.aggregateRootId())
                .withVersion(version)
                .withDescription(aggregateRoot.getString("description"))
                .withTodoStatus(TodoStatus.valueOf(aggregateRoot.getString("todoStatus")))
                .build();
    }

}
