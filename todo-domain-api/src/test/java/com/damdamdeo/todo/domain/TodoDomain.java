package com.damdamdeo.todo.domain;

import com.damdamdeo.todo.domain.api.Todo;
import com.damdamdeo.todo.domain.api.TodoStatus;

import java.util.Objects;

public class TodoDomain implements Todo {

    private final String todoId;
    private final String description;
    private final TodoStatus todoStatus;
    private final Long version;

    public TodoDomain(final String todoId,
                      final String description,
                      final TodoStatus todoStatus,
                      final Long version) {
        this.todoId = todoId;
        this.description = description;
        this.todoStatus = todoStatus;
        this.version = version;
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
        if (!(o instanceof TodoDomain)) return false;
        TodoDomain that = (TodoDomain) o;
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
