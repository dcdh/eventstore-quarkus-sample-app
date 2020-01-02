package com.damdamdeo.todo_graph_visualiser.domain;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Todo {

    private final String todoId;
    private final String description;
    private final String todoStatus;
    private final Integer version;
    private final List<Map<String, Object>> events;

    public Todo(final String todoId, final String description, final String todoStatus, final Integer version, final List<Map<String, Object>> events) {
        this.todoId = todoId;
        this.description = description;
        this.todoStatus = todoStatus;
        this.version = version;
        this.events = events;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Todo)) return false;
        Todo todo = (Todo) o;
        return Objects.equals(todoId, todo.todoId) &&
                Objects.equals(description, todo.description) &&
                Objects.equals(todoStatus, todo.todoStatus) &&
                Objects.equals(version, todo.version) &&
                Objects.equals(events, todo.events);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoId, description, todoStatus, version, events);
    }

    public String getTodoId() {
        return todoId;
    }

    public String getDescription() {
        return description;
    }

    public String getTodoStatus() {
        return todoStatus;
    }

    public Integer getVersion() {
        return version;
    }

    public List<Map<String, Object>> getEvents() {
        return events;
    }
}
