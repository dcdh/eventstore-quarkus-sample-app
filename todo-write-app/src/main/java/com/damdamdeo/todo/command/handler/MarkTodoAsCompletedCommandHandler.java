package com.damdamdeo.todo.command.handler;

import com.damdamdeo.eventdataspreader.writeside.command.api.Command;
import com.damdamdeo.eventdataspreader.writeside.command.api.CommandHandler;
import com.damdamdeo.eventdataspreader.writeside.command.api.CommandQualifier;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;
import com.damdamdeo.todo.aggregate.TodoAggregateRoot;
import com.damdamdeo.todo.aggregate.TodoAggregateRootRepository;
import com.damdamdeo.todo.command.MarkTodoAsCompletedCommand;

import javax.enterprise.context.Dependent;

@Dependent
@CommandQualifier(MarkTodoAsCompletedCommand.class)
public class MarkTodoAsCompletedCommandHandler implements CommandHandler {

    final TodoAggregateRootRepository todoAggregateRootRepository;

    public MarkTodoAsCompletedCommandHandler(final TodoAggregateRootRepository todoAggregateRootRepository) {
        this.todoAggregateRootRepository = todoAggregateRootRepository;
    }

    @Override
    public AggregateRoot handle(final Command command) {
        final TodoAggregateRoot todoAggregateRoot = todoAggregateRootRepository.load(command.aggregateId());
        final MarkTodoAsCompletedCommand markTodoAsCompletedCommand = (MarkTodoAsCompletedCommand) command;
        todoAggregateRoot.handle(markTodoAsCompletedCommand);
        return todoAggregateRootRepository.save(todoAggregateRoot);
    }

}
