package com.damdamdeo.email_notifier.infrastructure.consumer;

import com.damdamdeo.email_notifier.domain.usecase.NotifyTodoCreatedCommand;
import com.damdamdeo.email_notifier.domain.usecase.NotifyTodoCreatedUseCase;
import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumable;
import com.damdamdeo.eventsourced.consumer.api.eventsourcing.Operation;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.JsonObjectAggregateRootEventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import java.util.Objects;

@ApplicationScoped
public class TodoCreatedEventConsumer implements JsonObjectAggregateRootEventConsumer {

    private final Logger logger = LoggerFactory.getLogger(TodoCreatedEventConsumer.class);

    private final NotifyTodoCreatedUseCase notifyTodoCreatedUseCase;

    public TodoCreatedEventConsumer(final NotifyTodoCreatedUseCase notifyTodoCreatedUseCase) {
        this.notifyTodoCreatedUseCase = Objects.requireNonNull(notifyTodoCreatedUseCase);
    }

    @Override
    public void consume(final AggregateRootEventConsumable<JsonObject> aggregateRootEventConsumable, final Operation operation) {
        if (Operation.READ.equals(operation)) {
            return;
        }
        logger.info(String.format("Consuming '%s' for '%s'", eventType(), aggregateRootEventConsumable.eventId()));
        final JsonObjectTodo jsonObjectTodo = new JsonObjectTodo(aggregateRootEventConsumable.materializedState(),
                aggregateRootEventConsumable.eventId());
        notifyTodoCreatedUseCase.execute(new NotifyTodoCreatedCommand(jsonObjectTodo.todoDomain()));
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
