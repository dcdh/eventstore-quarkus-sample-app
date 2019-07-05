package com.damdamdeo.order.domain;

import com.damdamdeo.eventsourcing.domain.AggregateRoot;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.adapter.JsonbAdapter;

public class AggregateRootAdapter implements JsonbAdapter<AggregateRoot, JsonObject> {

    private final static String DISCRIMINATOR = "@class";

    @Override
    public JsonObject adaptToJson(final AggregateRoot aggregateRoot) throws Exception {
        final String aggregateRootTypeSimpleName = aggregateRoot.getClass().getSimpleName();
        switch (aggregateRootTypeSimpleName) {
            case "OrderAggregateRoot":
                return Json.createObjectBuilder()
                        .add(DISCRIMINATOR, aggregateRootTypeSimpleName)
                        .add("aggregateRootId", ((OrderAggregateRoot) aggregateRoot).aggregateRootId())
                        .add("articleName", ((OrderAggregateRoot) aggregateRoot).articleName())
                        .add("quantity", ((OrderAggregateRoot) aggregateRoot).quantity())
                        .add("send", ((OrderAggregateRoot) aggregateRoot).send())
                        .add("version", ((OrderAggregateRoot) aggregateRoot).version())
                        .build();
            default:
                throw new IllegalStateException("Unknown type : " + aggregateRootTypeSimpleName);
        }
    }

    @Override
    public AggregateRoot adaptFromJson(final JsonObject aggregateRoot) throws Exception {
        switch (aggregateRoot.getString(DISCRIMINATOR)) {
            case "OrderAggregateRoot":
                return new OrderAggregateRoot(
                        aggregateRoot.getString("aggregateRootId"),
                        aggregateRoot.getString("articleName"),
                        aggregateRoot.getJsonNumber("quantity").longValue(),
                        aggregateRoot.getBoolean("send"),
                        aggregateRoot.getJsonNumber("version").longValue()
                );
            default:
                throw new IllegalStateException("Unknown type : " + aggregateRoot.getString(DISCRIMINATOR));
        }
    }
}
