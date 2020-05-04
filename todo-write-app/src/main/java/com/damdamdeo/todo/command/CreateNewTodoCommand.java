package com.damdamdeo.todo.command;

import com.damdamdeo.eventdataspreader.writeside.command.api.Command;

import java.util.Objects;

public class CreateNewTodoCommand implements Command {

    private final String description;

    public CreateNewTodoCommand(final String description) {
        this.description = Objects.requireNonNull(description);
    }

    public String description() {
        return description;
    }

}
