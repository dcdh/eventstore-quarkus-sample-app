package com.damdamdeo.todo.command.handler;

import com.damdamdeo.eventdataspreader.writeside.command.api.AbstractCommandHandler;
import com.damdamdeo.eventdataspreader.writeside.command.api.CommandExecutor;
import com.damdamdeo.todo.aggregate.TodoAggregateRoot;
import com.damdamdeo.todo.aggregate.TodoAggregateRootRepository;
import com.damdamdeo.todo.domain.api.Todo;
import com.damdamdeo.todo.domain.api.TodoAlreadyExistentException;
import com.damdamdeo.todo.command.CreateNewTodoCommand;

import javax.enterprise.context.Dependent;

@Dependent
public class CreateNewTodoCommandHandler extends AbstractCommandHandler<TodoAggregateRoot, CreateNewTodoCommand> {

    final TodoAggregateRootRepository todoAggregateRootRepository;

    public CreateNewTodoCommandHandler(final TodoAggregateRootRepository todoAggregateRootRepository, final CommandExecutor commandExecutor) {
        super(commandExecutor);
        this.todoAggregateRootRepository = todoAggregateRootRepository;
    }

    @Override
    protected TodoAggregateRoot handle(final CreateNewTodoCommand createNewTodoCommand) {
        if (todoAggregateRootRepository.isTodoExistent(createNewTodoCommand.todoId())) {
            final Todo todoExistent = todoAggregateRootRepository.load(createNewTodoCommand.aggregateId());
            throw new TodoAlreadyExistentException(todoExistent);
        }
        final TodoAggregateRoot todoAggregateRoot = new TodoAggregateRoot();
        todoAggregateRoot.handle(createNewTodoCommand);
        return todoAggregateRootRepository.save(todoAggregateRoot);
    }
}
