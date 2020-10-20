package com.damdamdeo.todo.domain.command;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.command.Command;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.command.CommandLockingType;

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

    @Override
    public CommandLockingType commandLockingType() {
        return CommandLockingType.GLOBAL;
    }

    @Override
    public String aggregateRootId() {
        return null;
    }

}
