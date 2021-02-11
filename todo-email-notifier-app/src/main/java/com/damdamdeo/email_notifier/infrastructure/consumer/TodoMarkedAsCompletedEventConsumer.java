package com.damdamdeo.email_notifier.infrastructure.consumer;

import com.damdamdeo.email_notifier.domain.usecase.NotifyTodoMarkedAsCompletedCommand;
import com.damdamdeo.email_notifier.domain.usecase.NotifyTodoMarkedAsCompletedUseCase;
import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumable;
import com.damdamdeo.eventsourced.consumer.api.eventsourcing.Operation;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.JsonObjectAggregateRootEventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import java.util.Objects;

@ApplicationScoped
public class TodoMarkedAsCompletedEventConsumer implements JsonObjectAggregateRootEventConsumer {

    private final Logger logger = LoggerFactory.getLogger(TodoMarkedAsCompletedEventConsumer.class);

    private final NotifyTodoMarkedAsCompletedUseCase notifyTodoMarkedAsCompletedUseCase;

    public TodoMarkedAsCompletedEventConsumer(final NotifyTodoMarkedAsCompletedUseCase notifyTodoMarkedAsCompletedUseCase) {
        this.notifyTodoMarkedAsCompletedUseCase = Objects.requireNonNull(notifyTodoMarkedAsCompletedUseCase);
    }

    @Override
    public void consume(final AggregateRootEventConsumable<JsonObject> aggregateRootEventConsumable, final Operation operation) {
        if (Operation.READ.equals(operation)) {
            return;
        }
        logger.info(String.format("Consuming '%s' for '%s'", eventType(), aggregateRootEventConsumable.eventId()));
        final JsonObjectTodo jsonObjectTodo = new JsonObjectTodo(aggregateRootEventConsumable.materializedState(),
                aggregateRootEventConsumable.eventId());
        notifyTodoMarkedAsCompletedUseCase.execute(new NotifyTodoMarkedAsCompletedCommand(jsonObjectTodo.todoDomain()));
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
