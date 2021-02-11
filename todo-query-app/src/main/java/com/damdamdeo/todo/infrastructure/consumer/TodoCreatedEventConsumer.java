package com.damdamdeo.todo.infrastructure.consumer;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumable;
import com.damdamdeo.eventsourced.consumer.api.eventsourcing.Operation;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.JsonObjectAggregateRootEventConsumer;
import com.damdamdeo.todo.domain.usecase.CreateTodoCommand;
import com.damdamdeo.todo.domain.usecase.CreateTodoUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.transaction.Transactional;
import java.util.Objects;

@ApplicationScoped
public class TodoCreatedEventConsumer implements JsonObjectAggregateRootEventConsumer {

    private final Logger logger = LoggerFactory.getLogger(TodoCreatedEventConsumer.class);

    private final CreateTodoUseCase createTodoUseCase;

    public TodoCreatedEventConsumer(final CreateTodoUseCase createTodoUseCase) {
        this.createTodoUseCase = Objects.requireNonNull(createTodoUseCase);
    }

    @Override
    @Transactional
    public void consume(final AggregateRootEventConsumable<JsonObject> aggregateRootEventConsumable, final Operation operation) {
        logger.info(String.format("Consuming '%s' for '%s'", eventType(), aggregateRootEventConsumable.eventId()));
        createTodoUseCase.execute(new CreateTodoCommand(
                aggregateRootEventConsumable.eventPayload().getString("todoId"),
                aggregateRootEventConsumable.eventPayload().getString("description"),
                aggregateRootEventConsumable.eventId().version()
        ));
    }

    @Override
    public String aggregateRootType() {
        return "TodoAggregateRoot";
    }

    @Override
    public String eventType() {
        return "TodoCreatedEvent";
    }

}
