package com.damdamdeo.todo.command.handler;

import com.damdamdeo.eventdataspreader.writeside.command.api.Command;
import com.damdamdeo.eventdataspreader.writeside.command.api.CommandHandler;
import com.damdamdeo.eventdataspreader.writeside.command.api.CommandQualifier;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;
import com.damdamdeo.todo.aggregate.TodoAggregateRoot;
import com.damdamdeo.todo.aggregate.TodoAggregateRootRepository;
import com.damdamdeo.todo.api.TodoIdAlreadyExistentException;
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
            throw new TodoIdAlreadyExistentException(createNewTodoCommand.todoId());
        }
        final TodoAggregateRoot todoAggregateRoot = new TodoAggregateRoot();
        todoAggregateRoot.handle(createNewTodoCommand);
        return todoAggregateRootRepository.save(todoAggregateRoot);
    }

}
