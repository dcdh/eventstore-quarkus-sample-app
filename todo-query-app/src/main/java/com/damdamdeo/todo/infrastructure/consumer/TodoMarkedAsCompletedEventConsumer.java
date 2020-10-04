package com.damdamdeo.todo.infrastructure.consumer;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumable;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.JsonNodeAggregateRootEventConsumer;
import com.damdamdeo.todo.domain.MarkTodoAsCompletedService;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.Objects;

@ApplicationScoped
public class TodoMarkedAsCompletedEventConsumer implements JsonNodeAggregateRootEventConsumer {

    private final Logger logger = LoggerFactory.getLogger(TodoMarkedAsCompletedEventConsumer.class);

    private final MarkTodoAsCompletedService markTodoAsCompletedService;

    public TodoMarkedAsCompletedEventConsumer(final MarkTodoAsCompletedService markTodoAsCompletedService) {
        this.markTodoAsCompletedService = Objects.requireNonNull(markTodoAsCompletedService);
    }

    @Override
    @Transactional
    public void consume(final AggregateRootEventConsumable<JsonNode> aggregateRootEventConsumable) {
        logger.info(String.format("Consuming '%s' for '%s'", eventType(), aggregateRootEventConsumable.eventId()));
        markTodoAsCompletedService.markTodoAsCompleted(
                aggregateRootEventConsumable.eventPayload().get("todoId").asText(),
                aggregateRootEventConsumable.eventId().version());
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
