package com.damdamdeo.todo.command.handler;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.UnknownAggregateRootException;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.command.CommandHandler;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.command.CommandExecutorBinding;
import com.damdamdeo.todo.aggregate.TodoAggregateRoot;
import com.damdamdeo.todo.aggregate.TodoAggregateRootRepository;
import com.damdamdeo.todo.command.MarkTodoAsCompletedCommand;
import com.damdamdeo.todo.domain.api.UnknownTodoException;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class MarkTodoAsCompletedCommandHandler implements CommandHandler<TodoAggregateRoot, MarkTodoAsCompletedCommand> {

    final TodoAggregateRootRepository todoAggregateRootRepository;

    public MarkTodoAsCompletedCommandHandler(final TodoAggregateRootRepository todoAggregateRootRepository) {
        this.todoAggregateRootRepository = Objects.requireNonNull(todoAggregateRootRepository);
    }

    @CommandExecutorBinding
    @Override
    public TodoAggregateRoot execute(MarkTodoAsCompletedCommand markTodoAsCompletedCommand) throws Throwable {
        try {
            final TodoAggregateRoot todoAggregateRoot = todoAggregateRootRepository.load(markTodoAsCompletedCommand.todoId());
            todoAggregateRoot.canMarkTodoAsCompletedSpecification().checkSatisfiedBy(todoAggregateRoot);
            todoAggregateRoot.handle(markTodoAsCompletedCommand);
            return todoAggregateRootRepository.save(todoAggregateRoot);
        } catch (final UnknownAggregateRootException unknownAggregateRootException) {
            throw new UnknownTodoException(markTodoAsCompletedCommand.todoId());
        }
    }

}
