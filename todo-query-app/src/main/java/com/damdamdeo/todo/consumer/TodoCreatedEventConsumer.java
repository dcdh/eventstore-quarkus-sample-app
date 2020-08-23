package com.damdamdeo.todo.consumer;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumable;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.JsonNodeAggregateRootEventConsumer;
import com.damdamdeo.todo.domain.api.TodoStatus;
import com.damdamdeo.todo.infrastructure.JpaTodoRepository;
import com.damdamdeo.todo.infrastructure.TodoEntity;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class TodoCreatedEventConsumer implements JsonNodeAggregateRootEventConsumer {

    private final Logger logger = LoggerFactory.getLogger(TodoCreatedEventConsumer.class);

    final JpaTodoRepository jpaTodoRepository;

    public TodoCreatedEventConsumer(final JpaTodoRepository jpaTodoRepository) {
        this.jpaTodoRepository = Objects.requireNonNull(jpaTodoRepository);
    }

    @Override
    public void consume(final AggregateRootEventConsumable<JsonNode> aggregateRootEventConsumable) {
        logger.info(String.format("Consuming '%s' for '%s'", eventType(), aggregateRootEventConsumable.eventId()));
        final TodoEntity todoToCreate = new TodoEntity(
                aggregateRootEventConsumable.eventPayload().get("todoId").asText(),
                aggregateRootEventConsumable.eventPayload().get("description").asText(),
                TodoStatus.IN_PROGRESS,
                aggregateRootEventConsumable.eventId());
        jpaTodoRepository.persist(todoToCreate);
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
