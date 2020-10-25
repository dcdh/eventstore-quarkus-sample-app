package com.damdamdeo.email_notifier.infrastructure.consumer;

import com.damdamdeo.email_notifier.domain.TodoMarkedAsCompletedNotifierService;
import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumable;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.JsonObjectAggregateRootEventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import java.util.Objects;

@ApplicationScoped
public class TodoMarkedAsCompletedEventConsumer implements JsonObjectAggregateRootEventConsumer {

    private final Logger logger = LoggerFactory.getLogger(TodoMarkedAsCompletedEventConsumer.class);

    private final TodoMarkedAsCompletedNotifierService todoMarkedAsCompletedNotifierService;

    public TodoMarkedAsCompletedEventConsumer(final TodoMarkedAsCompletedNotifierService todoMarkedAsCompletedNotifierService) {
        this.todoMarkedAsCompletedNotifierService = Objects.requireNonNull(todoMarkedAsCompletedNotifierService);
    }

    @Override
    public void consume(final AggregateRootEventConsumable<JsonObject> aggregateRootEventConsumable) {
        logger.info(String.format("Consuming '%s' for '%s'", eventType(), aggregateRootEventConsumable.eventId()));
        final JsonObjectTodo jsonObjectTodo = new JsonObjectTodo(aggregateRootEventConsumable.materializedState(),
                aggregateRootEventConsumable.eventId());
        todoMarkedAsCompletedNotifierService.notify(jsonObjectTodo.todoDomain());
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
