package com.damdamdeo.todo.command;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.command.Command;

import java.util.Objects;

public final class CreateNewTodoCommand implements Command {

    private final String description;

    public CreateNewTodoCommand(final String description) {
        this.description = Objects.requireNonNull(description);
    }

    public String description() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateNewTodoCommand that = (CreateNewTodoCommand) o;
        return Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description);
    }
}
