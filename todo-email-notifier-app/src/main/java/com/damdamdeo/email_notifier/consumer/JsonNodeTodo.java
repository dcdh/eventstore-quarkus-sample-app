package com.damdamdeo.email_notifier.consumer;

import com.damdamdeo.email_notifier.domain.Todo;
import com.damdamdeo.email_notifier.domain.TodoStatus;
import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;

public final class JsonNodeTodo implements Todo {

    private final String todoId;
    private final String description;
    private final TodoStatus todoStatus;
    private final Long version;

    public JsonNodeTodo(final JsonNode jsonNode,
                        final AggregateRootEventId aggregateRootEventId) {
        this.todoId = jsonNode.get("todoId").asText();
        this.description = jsonNode.get("description").asText();
        this.todoStatus = TodoStatus.valueOf(jsonNode.get("todoStatus").asText());
        this.version = aggregateRootEventId.version();
    }

    @Override
    public String todoId() {
        return todoId;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public TodoStatus todoStatus() {
        return todoStatus;
    }

    @Override
    public Long version() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JsonNodeTodo)) return false;
        JsonNodeTodo that = (JsonNodeTodo) o;
        return Objects.equals(todoId, that.todoId) &&
                Objects.equals(description, that.description) &&
                todoStatus == that.todoStatus &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoId, description, todoStatus, version);
    }
}
