package com.damdamdeo.todo.domain.command.handler;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.command.CommandHandler;
import com.damdamdeo.todo.domain.TodoAggregateRoot;
import com.damdamdeo.todo.domain.command.CreateNewTodoCommand;

public interface CreateNewTodoCommandHandler extends CommandHandler<TodoAggregateRoot, CreateNewTodoCommand> {
}
