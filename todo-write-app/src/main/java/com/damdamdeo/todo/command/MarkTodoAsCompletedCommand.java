package com.damdamdeo.todo.command;

import com.damdamdeo.eventdataspreader.writeside.command.api.Command;

import java.util.Objects;

public class MarkTodoAsCompletedCommand implements Command {

    private final String todoId;

    public MarkTodoAsCompletedCommand(final String todoId) {
        this.todoId = Objects.requireNonNull(todoId);
    }

    public String todoId() {
        return todoId;
    }

    @Override
    public String aggregateId() {
        return todoId;
    }

}
