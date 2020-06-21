package com.damdamdeo.todo.command;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.command.Command;

import java.util.Objects;

public final class MarkTodoAsCompletedCommand implements Command {

    private final String todoId;

    public MarkTodoAsCompletedCommand(final String todoId) {
        this.todoId = Objects.requireNonNull(todoId);
    }

    public String todoId() {
        return todoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarkTodoAsCompletedCommand that = (MarkTodoAsCompletedCommand) o;
        return Objects.equals(todoId, that.todoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoId);
    }
}
