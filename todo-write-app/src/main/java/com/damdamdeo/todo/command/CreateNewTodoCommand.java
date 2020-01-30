package com.damdamdeo.todo.command;

import com.damdamdeo.eventdataspreader.writeside.command.api.Command;

import java.util.Objects;

public class CreateNewTodoCommand implements Command {

    private final String todoId;
    private final String description;

    public CreateNewTodoCommand(final String todoId,
                                final String description) {
        this.todoId = Objects.requireNonNull(todoId);
        this.description = Objects.requireNonNull(description);
    }

    @Override
    public String aggregateId() {
        return todoId;
    }

    public String todoId() {
        return todoId;
    }

    public String description() {
        return description;
    }

}
