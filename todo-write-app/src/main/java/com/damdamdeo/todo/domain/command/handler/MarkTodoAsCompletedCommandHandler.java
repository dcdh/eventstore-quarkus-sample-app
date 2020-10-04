package com.damdamdeo.todo.domain.command.handler;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.UnknownAggregateRootException;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.command.CommandHandler;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.command.CommandExecutorBinding;
import com.damdamdeo.todo.domain.TodoAggregateRoot;
import com.damdamdeo.todo.domain.TodoAggregateRootRepository;
import com.damdamdeo.todo.domain.command.MarkTodoAsCompletedCommand;
import com.damdamdeo.todo.domain.api.UnknownTodoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class MarkTodoAsCompletedCommandHandler implements CommandHandler<TodoAggregateRoot, MarkTodoAsCompletedCommand> {

    private final Logger logger = LoggerFactory.getLogger(MarkTodoAsCompletedCommandHandler.class);

    private final TodoAggregateRootRepository todoAggregateRootRepository;

    public MarkTodoAsCompletedCommandHandler(final TodoAggregateRootRepository todoAggregateRootRepository) {
        this.todoAggregateRootRepository = Objects.requireNonNull(todoAggregateRootRepository);
    }

    @CommandExecutorBinding
    @Override
    public TodoAggregateRoot execute(final MarkTodoAsCompletedCommand markTodoAsCompletedCommand) throws Throwable {
        logger.info(String.format("Handling '%s' for '%s'", "MarkTodoAsCompletedCommand", markTodoAsCompletedCommand.todoId()));
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
