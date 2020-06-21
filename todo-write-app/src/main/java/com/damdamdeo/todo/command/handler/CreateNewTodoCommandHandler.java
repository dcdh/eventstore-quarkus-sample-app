package com.damdamdeo.todo.command.handler;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.command.CommandHandler;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.command.CommandExecutorBinding;
import com.damdamdeo.todo.aggregate.TodoAggregateRoot;
import com.damdamdeo.todo.aggregate.TodoAggregateRootRepository;
import com.damdamdeo.todo.domain.api.Todo;
import com.damdamdeo.todo.domain.api.TodoAlreadyExistentException;
import com.damdamdeo.todo.command.CreateNewTodoCommand;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class CreateNewTodoCommandHandler implements CommandHandler<TodoAggregateRoot, CreateNewTodoCommand> {

    final TodoAggregateRootRepository todoAggregateRootRepository;
    final TodoIdGenerator todoIdGenerator;
    final NewTodoAggregateRootProvider newTodoAggregateRootProvider;

    public CreateNewTodoCommandHandler(final TodoAggregateRootRepository todoAggregateRootRepository,
                                       final TodoIdGenerator todoIdGenerator,
                                       final NewTodoAggregateRootProvider newTodoAggregateRootProvider) {
        this.todoIdGenerator = Objects.requireNonNull(todoIdGenerator);
        this.todoAggregateRootRepository = Objects.requireNonNull(todoAggregateRootRepository);
        this.newTodoAggregateRootProvider = Objects.requireNonNull(newTodoAggregateRootProvider);
    }

    @CommandExecutorBinding
    @Override
    public TodoAggregateRoot execute(CreateNewTodoCommand createNewTodoCommand) throws Throwable {
        final String generatedTodoId = todoIdGenerator.generateTodoId();
        if (todoAggregateRootRepository.isTodoExistent(generatedTodoId)) {
            final Todo todoExistent = todoAggregateRootRepository.load(generatedTodoId);
            // Should never happened !!
            throw new TodoAlreadyExistentException(todoExistent);
        }
        final TodoAggregateRoot todoAggregateRoot = newTodoAggregateRootProvider.create();
        todoAggregateRoot.handle(createNewTodoCommand, generatedTodoId);
        return todoAggregateRootRepository.save(todoAggregateRoot);
    }

}
