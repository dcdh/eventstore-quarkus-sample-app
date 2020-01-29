package com.damdamdeo.email_notifier.domain.event;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventPayload;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class TodoAggregateTodoMarkedAsCompletedEventPayload implements EventPayload {

    private final String todoId;

    @JsonCreator
    public TodoAggregateTodoMarkedAsCompletedEventPayload(@JsonProperty("todoId") final String todoId) {
        this.todoId = Objects.requireNonNull(todoId);
    }

    public String todoId() {
        return todoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TodoAggregateTodoMarkedAsCompletedEventPayload)) return false;
        TodoAggregateTodoMarkedAsCompletedEventPayload that = (TodoAggregateTodoMarkedAsCompletedEventPayload) o;
        return Objects.equals(todoId, that.todoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoId);
    }

    @Override
    public String toString() {
        return "TodoAggregateTodoMarkedAsCompletedEventPayload{" +
                "todoId='" + todoId + '\'' +
                '}';
    }
}
