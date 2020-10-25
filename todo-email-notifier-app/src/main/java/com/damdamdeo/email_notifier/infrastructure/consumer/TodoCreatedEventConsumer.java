package com.damdamdeo.email_notifier.infrastructure.consumer;

import com.damdamdeo.email_notifier.domain.TodoCreatedNotifierService;
import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumable;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.JsonObjectAggregateRootEventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import java.util.Objects;

@ApplicationScoped
public class TodoCreatedEventConsumer implements JsonObjectAggregateRootEventConsumer {

    private final Logger logger = LoggerFactory.getLogger(TodoCreatedEventConsumer.class);

    private final TodoCreatedNotifierService todoCreatedNotifierService;

    public TodoCreatedEventConsumer(final TodoCreatedNotifierService todoCreatedNotifierService) {
        this.todoCreatedNotifierService = Objects.requireNonNull(todoCreatedNotifierService);
    }

    @Override
    public void consume(final AggregateRootEventConsumable<JsonObject> aggregateRootEventConsumable) {
        logger.info(String.format("Consuming '%s' for '%s'", eventType(), aggregateRootEventConsumable.eventId()));
        final JsonObjectTodo jsonObjectTodo = new JsonObjectTodo(aggregateRootEventConsumable.materializedState(),
                aggregateRootEventConsumable.eventId());
        todoCreatedNotifierService.notify(jsonObjectTodo.todoDomain());
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
