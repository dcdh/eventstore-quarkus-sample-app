package com.damdamdeo.todo.domain.usecase;

import com.damdamdeo.todo.domain.api.usecase.UseCaseCommand;

import java.util.Objects;

public final class CreateTodoCommand implements UseCaseCommand {

    private final String todoId;
    private final String description;
    private final Long version;

    public CreateTodoCommand(final String todoId, final String description, final Long version) {
        this.todoId = Objects.requireNonNull(todoId);
        this.description = Objects.requireNonNull(description);
        this.version = Objects.requireNonNull(version);
    }

    public String todoId() {
        return todoId;
    }

    public String description() {
        return description;
    }

    public Long version() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CreateTodoCommand)) return false;
        CreateTodoCommand that = (CreateTodoCommand) o;
        return Objects.equals(todoId, that.todoId) &&
                Objects.equals(description, that.description) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoId, description, version);
    }
}
