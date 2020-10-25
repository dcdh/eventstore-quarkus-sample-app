package com.damdamdeo.todo.infrastructure.deserializer;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRootEventPayload;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.JsonbAggregateRootEventPayloadDeSerializer;
import com.damdamdeo.todo.domain.event.TodoMarkedAsCompletedEventPayload;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;

@ApplicationScoped
public class TodoMarkedAsCompletedEventPayloadDeSerializer implements JsonbAggregateRootEventPayloadDeSerializer {

    @Override
    public String aggregateRootType() {
        return "TodoAggregateRoot";
    }

    @Override
    public String eventType() {
        return "TodoMarkedAsCompletedEvent";
    }

    @Override
    public JsonObject encode(final AggregateRootId aggregateRootId, final AggregateRootEventPayload aggregateRootEventPayload) {
        final TodoMarkedAsCompletedEventPayload todoMarkedAsCompletedEventPayload = (TodoMarkedAsCompletedEventPayload) aggregateRootEventPayload;
        return Json.createObjectBuilder()
                .add("todoId", todoMarkedAsCompletedEventPayload.todoId())
                .build();
    }

    @Override
    public AggregateRootEventPayload decode(final JsonObject json) {
        return new TodoMarkedAsCompletedEventPayload(
                json.getString("todoId")
        );
    }

}
