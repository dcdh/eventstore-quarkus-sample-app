package com.damdamdeo.todo.domain.usecase;

import com.damdamdeo.todo.domain.api.usecase.UseCaseCommand;

import java.util.Objects;

public final class MarkTodoAsCompletedCommand implements UseCaseCommand {

    private final String todoId;
    private final Long version;

    public MarkTodoAsCompletedCommand(final String todoId, final Long version) {
        this.todoId = Objects.requireNonNull(todoId);
        this.version = Objects.requireNonNull(version);
    }

    public String todoId() {
        return todoId;
    }

    public Long version() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MarkTodoAsCompletedCommand)) return false;
        MarkTodoAsCompletedCommand that = (MarkTodoAsCompletedCommand) o;
        return Objects.equals(todoId, that.todoId) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoId, version);
    }
}
