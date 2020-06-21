package com.damdamdeo.email_notifier.consumer;

import com.damdamdeo.email_notifier.domain.TodoMarkedAsCompleted;

import java.util.Objects;

public final class DefaultTodoMarkedAsCompleted implements TodoMarkedAsCompleted {

    private final String todoId;
    private final String description;

    public DefaultTodoMarkedAsCompleted(final String todoId,
                                        final String description) {
        this.todoId = todoId;
        this.description = description;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultTodoMarkedAsCompleted that = (DefaultTodoMarkedAsCompleted) o;
        return Objects.equals(todoId, that.todoId) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoId, description);
    }
}
