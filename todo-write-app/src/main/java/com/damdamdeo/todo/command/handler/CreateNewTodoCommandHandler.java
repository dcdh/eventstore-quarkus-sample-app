package com.damdamdeo.todo.command.handler;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.command.CommandHandler;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.command.CommandExecutorBinding;
import com.damdamdeo.todo.aggregate.TodoAggregateRoot;
import com.damdamdeo.todo.aggregate.TodoAggregateRootRepository;
import com.damdamdeo.todo.domain.api.Todo;
import com.damdamdeo.todo.domain.api.TodoAlreadyExistentException;
import com.damdamdeo.todo.command.CreateNewTodoCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class CreateNewTodoCommandHandler implements CommandHandler<TodoAggregateRoot, CreateNewTodoCommand> {

    private final Logger logger = LoggerFactory.getLogger(CreateNewTodoCommandHandler.class);

    private final TodoAggregateRootRepository todoAggregateRootRepository;
    private final TodoIdGenerator todoIdGenerator;
    private final NewTodoAggregateRootProvider newTodoAggregateRootProvider;

    public CreateNewTodoCommandHandler(final TodoAggregateRootRepository todoAggregateRootRepository,
                                       final TodoIdGenerator todoIdGenerator,
                                       final NewTodoAggregateRootProvider newTodoAggregateRootProvider) {
        this.todoIdGenerator = Objects.requireNonNull(todoIdGenerator);
        this.todoAggregateRootRepository = Objects.requireNonNull(todoAggregateRootRepository);
        this.newTodoAggregateRootProvider = Objects.requireNonNull(newTodoAggregateRootProvider);
    }

    @CommandExecutorBinding
    @Override
    public TodoAggregateRoot execute(final CreateNewTodoCommand createNewTodoCommand) throws Throwable {
        logger.info(String.format("Handling '%s'", "CreateNewTodoCommand"));
        final String generatedTodoId = todoIdGenerator.generateTodoId();
        if (todoAggregateRootRepository.isTodoExistent(generatedTodoId)) {
            final Todo todoExistent = todoAggregateRootRepository.load(generatedTodoId);
            // Should never happened !!
            throw new TodoAlreadyExistentException(todoExistent);
        }
        final TodoAggregateRoot todoAggregateRoot = newTodoAggregateRootProvider.create(generatedTodoId);
        todoAggregateRoot.handle(createNewTodoCommand, generatedTodoId);
        return todoAggregateRootRepository.save(todoAggregateRoot);
    }

}
