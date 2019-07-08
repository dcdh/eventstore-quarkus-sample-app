package com.damdamdeo.todo.domain;

import com.damdamdeo.eventsourcing.domain.AggregateRoot;
import com.damdamdeo.todo.api.TodoStatus;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.adapter.JsonbAdapter;

public class AggregateRootAdapter implements JsonbAdapter<AggregateRoot, JsonObject> {

    private final static String DISCRIMINATOR = "@class";

    @Override
    public JsonObject adaptToJson(final AggregateRoot aggregateRoot) throws Exception {
        final String aggregateRootTypeSimpleName = aggregateRoot.getClass().getSimpleName();
        switch (aggregateRootTypeSimpleName) {
            case "TodoAggregateRoot":
                return Json.createObjectBuilder()
                        .add(DISCRIMINATOR, aggregateRootTypeSimpleName)
                        .add("aggregateRootId", aggregateRoot.aggregateRootId())
                        .add("description", ((TodoAggregateRoot) aggregateRoot).description())
                        .add("todoStatus", ((TodoAggregateRoot) aggregateRoot).todoStatus().name())
                        .add("version", aggregateRoot.version())
                        .build();
            default:
                throw new IllegalStateException("Unknown type : " + aggregateRootTypeSimpleName);
        }
    }

    @Override
    public AggregateRoot adaptFromJson(final JsonObject aggregateRoot) throws Exception {
        switch (aggregateRoot.getString(DISCRIMINATOR)) {
            case "TodoAggregateRoot":
                return new TodoAggregateRoot(
                        aggregateRoot.getString("aggregateRootId"),
                        aggregateRoot.getString("description"),
                        TodoStatus.valueOf(aggregateRoot.getString("todoStatus")),
                        aggregateRoot.getJsonNumber("version").longValue()
                );
            default:
                throw new IllegalStateException("Unknown type : " + aggregateRoot.getString(DISCRIMINATOR));
        }
    }
}
