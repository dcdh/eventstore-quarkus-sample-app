package com.damdamdeo.todo.command.handler;

import com.damdamdeo.eventdataspreader.writeside.command.api.AbstractCommandHandler;
import com.damdamdeo.eventdataspreader.writeside.command.api.CommandExecutor;
import com.damdamdeo.todo.aggregate.TodoAggregateRoot;
import com.damdamdeo.todo.aggregate.TodoAggregateRootRepository;
import com.damdamdeo.todo.domain.api.Todo;
import com.damdamdeo.todo.domain.api.TodoAlreadyExistentException;
import com.damdamdeo.todo.command.CreateNewTodoCommand;

import javax.enterprise.context.Dependent;
import java.util.Objects;

@Dependent
public class CreateNewTodoCommandHandler extends AbstractCommandHandler<TodoAggregateRoot, CreateNewTodoCommand> {

    final TodoAggregateRootRepository todoAggregateRootRepository;
    final TodoIdGenerator todoIdGenerator;

    public CreateNewTodoCommandHandler(final TodoAggregateRootRepository todoAggregateRootRepository,
                                       final TodoIdGenerator todoIdGenerator,
                                       final CommandExecutor commandExecutor) {
        super(commandExecutor);
        this.todoIdGenerator = Objects.requireNonNull(todoIdGenerator);
        this.todoAggregateRootRepository = todoAggregateRootRepository;
    }

    @Override
    protected TodoAggregateRoot handle(final CreateNewTodoCommand createNewTodoCommand) {
        final String generatedTodoId = todoIdGenerator.generateTodoId();
        if (todoAggregateRootRepository.isTodoExistent(generatedTodoId)) {
            final Todo todoExistent = todoAggregateRootRepository.load(generatedTodoId);
            // Should never happened !!
            throw new TodoAlreadyExistentException(todoExistent);
        }
        final TodoAggregateRoot todoAggregateRoot = new TodoAggregateRoot();
        todoAggregateRoot.handle(createNewTodoCommand, generatedTodoId);
        return todoAggregateRootRepository.save(todoAggregateRoot);
    }
}
