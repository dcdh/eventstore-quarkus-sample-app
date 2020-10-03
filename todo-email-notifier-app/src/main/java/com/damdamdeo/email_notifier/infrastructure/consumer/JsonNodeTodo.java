package com.damdamdeo.email_notifier.infrastructure.consumer;

import com.damdamdeo.email_notifier.domain.TodoDomain;
import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;

public final class JsonNodeTodo {

    private final TodoDomain todoDomain;

    public JsonNodeTodo(final JsonNode jsonNode,
                        final AggregateRootEventId aggregateRootEventId) {
        this.todoDomain = TodoDomain.newBuilder()
                .withTodoId(jsonNode.get("todoId").asText())
                .withDescription(jsonNode.get("description").asText())
                .build();
    }

    public TodoDomain todoDomain() {
        return todoDomain;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JsonNodeTodo)) return false;
        JsonNodeTodo that = (JsonNodeTodo) o;
        return Objects.equals(todoDomain, that.todoDomain);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoDomain);
    }
}
