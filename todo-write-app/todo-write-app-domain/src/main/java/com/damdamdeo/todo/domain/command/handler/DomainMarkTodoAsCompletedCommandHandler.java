package com.damdamdeo.todo.domain.command.handler;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.UnknownAggregateRootException;
import com.damdamdeo.todo.domain.TodoAggregateRoot;
import com.damdamdeo.todo.domain.TodoAggregateRootRepository;
import com.damdamdeo.todo.domain.api.UnknownTodoException;
import com.damdamdeo.todo.domain.api.specification.IsTodoNotMarkedAsCompletedSpecification;
import com.damdamdeo.todo.domain.command.MarkTodoAsCompletedCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public final class DomainMarkTodoAsCompletedCommandHandler implements MarkTodoAsCompletedCommandHandler {

    private final Logger logger = LoggerFactory.getLogger(DomainMarkTodoAsCompletedCommandHandler.class);

    private final TodoAggregateRootRepository todoAggregateRootRepository;
    private final IsTodoNotMarkedAsCompletedSpecification isTodoNotMarkedAsCompletedSpecification;

    public DomainMarkTodoAsCompletedCommandHandler(final TodoAggregateRootRepository todoAggregateRootRepository,
                                                   final IsTodoNotMarkedAsCompletedSpecification isTodoNotMarkedAsCompletedSpecification) {
        this.todoAggregateRootRepository = Objects.requireNonNull(todoAggregateRootRepository);
        this.isTodoNotMarkedAsCompletedSpecification = Objects.requireNonNull(isTodoNotMarkedAsCompletedSpecification);
    }

    @Override
    public TodoAggregateRoot execute(final MarkTodoAsCompletedCommand markTodoAsCompletedCommand) throws Throwable {
        logger.info(String.format("Handling '%s' for '%s'", "MarkTodoAsCompletedCommand", markTodoAsCompletedCommand.todoId()));
        try {
            final TodoAggregateRoot todoAggregateRoot = todoAggregateRootRepository.load(markTodoAsCompletedCommand.todoId());
            todoAggregateRoot.handle(markTodoAsCompletedCommand, isTodoNotMarkedAsCompletedSpecification);
            return todoAggregateRootRepository.save(todoAggregateRoot);
        } catch (final UnknownAggregateRootException unknownAggregateRootException) {
            throw new UnknownTodoException(markTodoAsCompletedCommand.todoId());
        }
    }

}
