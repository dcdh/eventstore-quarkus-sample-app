package com.damdamdeo.todo.command.handler;

import com.damdamdeo.eventdataspreader.writeside.command.api.AbstractCommandHandler;
import com.damdamdeo.eventdataspreader.writeside.command.api.CommandExecutor;
import com.damdamdeo.todo.aggregate.TodoAggregateRoot;
import com.damdamdeo.todo.aggregate.TodoAggregateRootRepository;
import com.damdamdeo.todo.command.MarkTodoAsCompletedCommand;

import javax.enterprise.context.Dependent;
import java.util.Objects;

@Dependent
public class MarkTodoAsCompletedCommandHandler extends AbstractCommandHandler<TodoAggregateRoot, MarkTodoAsCompletedCommand> {

    final TodoAggregateRootRepository todoAggregateRootRepository;

    public MarkTodoAsCompletedCommandHandler(final TodoAggregateRootRepository todoAggregateRootRepository, final CommandExecutor commandExecutor) {
        super(commandExecutor);
        this.todoAggregateRootRepository = Objects.requireNonNull(todoAggregateRootRepository);
    }

    @Override
    protected TodoAggregateRoot handle(final MarkTodoAsCompletedCommand markTodoAsCompletedCommand) {
        final TodoAggregateRoot todoAggregateRoot = todoAggregateRootRepository.load(markTodoAsCompletedCommand.todoId());
        todoAggregateRoot.canMarkTodoAsCompletedSpecification().checkSatisfiedBy(todoAggregateRoot);
        todoAggregateRoot.handle(markTodoAsCompletedCommand);
        return todoAggregateRootRepository.save(todoAggregateRoot);
    }
}
