package com.damdamdeo.todo.domain.command;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.command.Command;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.command.CommandLockingType;

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

    @Override
    public CommandLockingType commandLockingType() {
        return CommandLockingType.AGGREGATE_ONLY;
    }

    @Override
    public String aggregateRootId() {
        return todoId;
    }
}
