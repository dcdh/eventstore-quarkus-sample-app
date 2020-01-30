package com.damdamdeo.todo.command.handler;

import com.damdamdeo.eventdataspreader.writeside.command.api.Command;
import com.damdamdeo.eventdataspreader.writeside.command.api.CommandHandler;
import com.damdamdeo.eventdataspreader.writeside.command.api.CommandQualifier;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;
import com.damdamdeo.todo.aggregate.TodoAggregateRoot;
import com.damdamdeo.todo.aggregate.TodoAggregateRootRepository;
import com.damdamdeo.todo.domain.api.Todo;
import com.damdamdeo.todo.domain.api.TodoAlreadyExistentException;
import com.damdamdeo.todo.command.CreateNewTodoCommand;

import javax.enterprise.context.Dependent;

@Dependent
@CommandQualifier(CreateNewTodoCommand.class)
public class CreateNewTodoCommandHandler implements CommandHandler {

    final TodoAggregateRootRepository todoAggregateRootRepository;

    public CreateNewTodoCommandHandler(final TodoAggregateRootRepository todoAggregateRootRepository) {
        this.todoAggregateRootRepository = todoAggregateRootRepository;
    }

    @Override
    public AggregateRoot handle(final Command command) {
        final CreateNewTodoCommand createNewTodoCommand = (CreateNewTodoCommand) command;
        if (todoAggregateRootRepository.isTodoExistent(createNewTodoCommand.todoId())) {
            final Todo todoExistent = todoAggregateRootRepository.load(createNewTodoCommand.aggregateId());
            throw new TodoAlreadyExistentException(todoExistent);
        }
        final TodoAggregateRoot todoAggregateRoot = new TodoAggregateRoot();
        todoAggregateRoot.handle(createNewTodoCommand);
        return todoAggregateRootRepository.save(todoAggregateRoot);
    }

}
