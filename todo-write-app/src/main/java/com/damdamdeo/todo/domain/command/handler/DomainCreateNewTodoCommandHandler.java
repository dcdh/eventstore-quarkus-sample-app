package com.damdamdeo.todo.domain.command.handler;

import com.damdamdeo.todo.domain.TodoAggregateRoot;
import com.damdamdeo.todo.domain.TodoAggregateRootRepository;
import com.damdamdeo.todo.domain.api.Todo;
import com.damdamdeo.todo.domain.api.TodoAlreadyExistentException;
import com.damdamdeo.todo.domain.command.CreateNewTodoCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public final class DomainCreateNewTodoCommandHandler implements CreateNewTodoCommandHandler {

    private final Logger logger = LoggerFactory.getLogger(DomainCreateNewTodoCommandHandler.class);

    private final TodoAggregateRootRepository todoAggregateRootRepository;
    private final TodoIdGenerator todoIdGenerator;
    private final NewTodoAggregateRootProvider newTodoAggregateRootProvider;

    public DomainCreateNewTodoCommandHandler(final TodoAggregateRootRepository todoAggregateRootRepository,
                                             final TodoIdGenerator todoIdGenerator,
                                             final NewTodoAggregateRootProvider newTodoAggregateRootProvider) {
        this.todoIdGenerator = Objects.requireNonNull(todoIdGenerator);
        this.todoAggregateRootRepository = Objects.requireNonNull(todoAggregateRootRepository);
        this.newTodoAggregateRootProvider = Objects.requireNonNull(newTodoAggregateRootProvider);
    }

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
