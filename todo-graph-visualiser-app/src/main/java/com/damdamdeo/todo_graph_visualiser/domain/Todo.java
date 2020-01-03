package com.damdamdeo.todo_graph_visualiser.domain;

import java.util.Objects;

public final class Todo {

    private final String todoId;
    private final String description;
    private final String todoStatus;
    private final Integer version;

    public Todo(final String todoId,
                final String description,
                final String todoStatus,
                final Integer version) {
        this.todoId = todoId;
        this.description = description;
        this.todoStatus = todoStatus;
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Todo)) return false;
        Todo todo = (Todo) o;
        return Objects.equals(todoId, todo.todoId) &&
                Objects.equals(description, todo.description) &&
                Objects.equals(todoStatus, todo.todoStatus) &&
                Objects.equals(version, todo.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoId, description, todoStatus, version);
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

}
