package com.damdamdeo.todo.domain.api.event;

import com.damdamdeo.eventdataspreader.event.api.EventPayload;
import com.damdamdeo.eventdataspreader.eventsourcing.infrastructure.JacksonEncryptionDeserializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class TodoAggregateTodoCreatedEventPayload implements EventPayload {

    private final String todoId;

    @JsonDeserialize(using = JacksonEncryptionDeserializer.class)
    private final String description;

    @JsonCreator
    public TodoAggregateTodoCreatedEventPayload(@JsonProperty("todoId") final String todoId,
                                                @JsonProperty("description") final String description) {
        this.todoId = Objects.requireNonNull(todoId);
        this.description = Objects.requireNonNull(description);
    }

    public String todoId() {
        return todoId;
    }

    public String description() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TodoAggregateTodoCreatedEventPayload)) return false;
        TodoAggregateTodoCreatedEventPayload that = (TodoAggregateTodoCreatedEventPayload) o;
        return Objects.equals(todoId, that.todoId) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoId, description);
    }

    @Override
    public String toString() {
        return "TodoAggregateTodoCreatedEventPayload{" +
                "todoId='" + todoId + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
