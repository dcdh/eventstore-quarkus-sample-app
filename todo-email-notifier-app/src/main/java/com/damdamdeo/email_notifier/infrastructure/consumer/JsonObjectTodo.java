package com.damdamdeo.email_notifier.infrastructure.consumer;

import com.damdamdeo.email_notifier.domain.TodoDomain;
import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;

import javax.json.JsonObject;
import java.util.Objects;

public final class JsonObjectTodo {

    private final TodoDomain todoDomain;

    public JsonObjectTodo(final JsonObject jsonObject,
                          final AggregateRootEventId aggregateRootEventId) {
        this.todoDomain = TodoDomain.newBuilder()
                .withTodoId(jsonObject.getString("todoId"))
                .withDescription(jsonObject.getString("description"))
                .build();
    }

    public TodoDomain todoDomain() {
        return todoDomain;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JsonObjectTodo)) return false;
        JsonObjectTodo that = (JsonObjectTodo) o;
        return Objects.equals(todoDomain, that.todoDomain);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoDomain);
    }
}
