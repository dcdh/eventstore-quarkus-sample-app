package com.damdamdeo.todo.aggregate.event;

import com.damdamdeo.eventdataspreader.eventsourcing.infrastructure.JacksonEncryptionDeserializer;
import com.damdamdeo.eventdataspreader.eventsourcing.infrastructure.JacksonEncryptionSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootEventPayload;
import com.damdamdeo.todo.aggregate.TodoAggregateRoot;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class TodoAggregateTodoCreatedAggregateRootEventPayload extends AggregateRootEventPayload<TodoAggregateRoot> {

    private final String todoId;

    @JsonSerialize(using = JacksonEncryptionSerializer.class)
    @JsonDeserialize(using = JacksonEncryptionDeserializer.class)
    private final String description;

    @JsonCreator
    public TodoAggregateTodoCreatedAggregateRootEventPayload(@JsonProperty("todoId") final String todoId,
                                                             @JsonProperty("description") final String description) {
        this.todoId = Objects.requireNonNull(todoId);
        this.description = Objects.requireNonNull(description);
    }

    @Override
    protected void apply(final TodoAggregateRoot aggregateRoot) {
        aggregateRoot.on(this);
    }

    @Override
    public String eventName() {
        return "TodoCreatedEvent";
    }

    @Override
    public String aggregateRootId() {
        return todoId;
    }

    @Override
    public String aggregateRootType() {
        return "TodoAggregateRoot";
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
        if (o == null || getClass() != o.getClass()) return false;
        TodoAggregateTodoCreatedAggregateRootEventPayload that = (TodoAggregateTodoCreatedAggregateRootEventPayload) o;
        return Objects.equals(todoId, that.todoId) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoId, description);
    }

    @Override
    public String toString() {
        return "TodoCreatedEventPayload{" +
                "todoId='" + todoId + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
