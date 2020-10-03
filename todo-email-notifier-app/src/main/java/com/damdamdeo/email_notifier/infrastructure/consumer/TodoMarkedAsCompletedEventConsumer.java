package com.damdamdeo.email_notifier.infrastructure.consumer;

import com.damdamdeo.email_notifier.domain.TodoMarkedAsCompletedNotifierService;
import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumable;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.JsonNodeAggregateRootEventConsumer;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class TodoMarkedAsCompletedEventConsumer implements JsonNodeAggregateRootEventConsumer {

    private final Logger logger = LoggerFactory.getLogger(TodoMarkedAsCompletedEventConsumer.class);

    private final TodoMarkedAsCompletedNotifierService todoMarkedAsCompletedNotifierService;

    public TodoMarkedAsCompletedEventConsumer(final TodoMarkedAsCompletedNotifierService todoMarkedAsCompletedNotifierService) {
        this.todoMarkedAsCompletedNotifierService = Objects.requireNonNull(todoMarkedAsCompletedNotifierService);
    }

    @Override
    public void consume(final AggregateRootEventConsumable<JsonNode> aggregateRootEventConsumable) {
        logger.info(String.format("Consuming '%s' for '%s'", eventType(), aggregateRootEventConsumable.eventId()));
        final JsonNodeTodo jsonNodeTodo = new JsonNodeTodo(aggregateRootEventConsumable.materializedState(),
                aggregateRootEventConsumable.eventId());
        todoMarkedAsCompletedNotifierService.notify(jsonNodeTodo.todoDomain());
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
