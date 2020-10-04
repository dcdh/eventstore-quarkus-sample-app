package com.damdamdeo.todo.domain.command.handler;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.command.CommandHandler;
import com.damdamdeo.todo.domain.TodoAggregateRoot;
import com.damdamdeo.todo.domain.command.MarkTodoAsCompletedCommand;

public interface MarkTodoAsCompletedCommandHandler extends CommandHandler<TodoAggregateRoot, MarkTodoAsCompletedCommand> {
}
