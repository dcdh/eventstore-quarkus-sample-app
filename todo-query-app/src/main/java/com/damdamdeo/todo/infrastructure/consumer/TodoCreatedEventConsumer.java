package com.damdamdeo.todo.infrastructure.consumer;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumable;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.JsonNodeAggregateRootEventConsumer;
import com.damdamdeo.todo.domain.CreateTodoService;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.util.Objects;

@ApplicationScoped
public class TodoCreatedEventConsumer implements JsonNodeAggregateRootEventConsumer {

    private final Logger logger = LoggerFactory.getLogger(TodoCreatedEventConsumer.class);

    private final CreateTodoService createTodoService;

    public TodoCreatedEventConsumer(final CreateTodoService createTodoService) {
        this.createTodoService = Objects.requireNonNull(createTodoService);
    }

    @Override
    @Transactional
    public void consume(final AggregateRootEventConsumable<JsonNode> aggregateRootEventConsumable) {
        logger.info(String.format("Consuming '%s' for '%s'", eventType(), aggregateRootEventConsumable.eventId()));
        createTodoService.createTodo(
                aggregateRootEventConsumable.eventPayload().get("todoId").asText(),
                aggregateRootEventConsumable.eventPayload().get("description").asText(),
                aggregateRootEventConsumable.eventId().version());
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
