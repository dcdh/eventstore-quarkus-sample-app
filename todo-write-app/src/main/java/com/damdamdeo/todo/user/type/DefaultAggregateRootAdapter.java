package com.damdamdeo.todo.user.type;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.hibernate.user.types.AggregateRootAdapter;
import com.damdamdeo.todo.aggregate.TodoAggregateRoot;
import com.damdamdeo.todo.api.TodoStatus;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.adapter.JsonbAdapter;

public class DefaultAggregateRootAdapter implements AggregateRootAdapter,
        JsonbAdapter<AggregateRoot, JsonObject> // Quick fix an issue with yasson
    {

    private final static String AGGREGATE_ROOT_TYPE = "@aggregaterootType";

    private static final String TODO_AGGREGATE_ROOT = "TodoAggregateRoot";

    private static final String AGGREGATE_ROOT_ID = "aggregateRootId";
    private static final String DESCRIPTION = "description";
    private static final String TODO_STATUS = "todoStatus";
    private static final String VERSION = "version";

    @Override
    public JsonObject adaptToJson(final AggregateRoot aggregateRoot) throws Exception {
        final String aggregateRootTypeSimpleName = aggregateRoot.getClass().getSimpleName();
        switch (aggregateRootTypeSimpleName) {
            case TODO_AGGREGATE_ROOT:
                return Json.createObjectBuilder()
                        .add(AGGREGATE_ROOT_TYPE, aggregateRootTypeSimpleName)
                        .add(AGGREGATE_ROOT_ID, aggregateRoot.aggregateRootId())
                        .add(DESCRIPTION, ((TodoAggregateRoot) aggregateRoot).description())
                        .add(TODO_STATUS, ((TodoAggregateRoot) aggregateRoot).todoStatus().name())
                        .add(VERSION, aggregateRoot.version())
                        .build();
            default:
                throw new IllegalStateException("Unknown aggregate type : " + aggregateRootTypeSimpleName);
        }
    }

    @Override
    public AggregateRoot adaptFromJson(final JsonObject aggregateRoot) throws Exception {
        switch (aggregateRoot.getString(AGGREGATE_ROOT_TYPE)) {
            case TODO_AGGREGATE_ROOT:
                return new TodoAggregateRoot(
                        aggregateRoot.getString(AGGREGATE_ROOT_ID),
                        aggregateRoot.getString(DESCRIPTION),
                        TodoStatus.valueOf(aggregateRoot.getString(TODO_STATUS)),
                        aggregateRoot.getJsonNumber(VERSION).longValue()
                );
            default:
                throw new IllegalStateException("Unknown type : " + aggregateRoot.getString(AGGREGATE_ROOT_TYPE));
        }
    }

}
