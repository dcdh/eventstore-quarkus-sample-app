package com.damdamdeo.todo.infrastructure.consumer;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumable;
import com.damdamdeo.eventsourced.consumer.api.eventsourcing.Operation;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.JsonObjectAggregateRootEventConsumer;
import com.damdamdeo.todo.domain.usecase.MarkTodoAsCompletedCommand;
import com.damdamdeo.todo.domain.usecase.MarkTodoAsCompletedUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.transaction.Transactional;
import java.util.Objects;

@ApplicationScoped
public class TodoMarkedAsCompletedEventConsumer implements JsonObjectAggregateRootEventConsumer {

    private final Logger logger = LoggerFactory.getLogger(TodoMarkedAsCompletedEventConsumer.class);

    private final MarkTodoAsCompletedUseCase markTodoAsCompletedUseCase;

    public TodoMarkedAsCompletedEventConsumer(final MarkTodoAsCompletedUseCase markTodoAsCompletedUseCase) {
        this.markTodoAsCompletedUseCase = Objects.requireNonNull(markTodoAsCompletedUseCase);
    }

    @Override
    @Transactional
    public void consume(final AggregateRootEventConsumable<JsonObject> aggregateRootEventConsumable, final Operation operation) {
        logger.info(String.format("Consuming '%s' for '%s'", eventType(), aggregateRootEventConsumable.eventId()));
        markTodoAsCompletedUseCase.execute(new MarkTodoAsCompletedCommand(
                aggregateRootEventConsumable.eventPayload().getString("todoId"),
                aggregateRootEventConsumable.eventId().version()));
    }

    @Override
    public String aggregateRootType() {
        return "TodoAggregateRoot";
    }

    @Override
    public String eventType() {
        return "TodoMarkedAsCompletedEvent";
    }

}
