package com.damdamdeo.todo.consumer;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumable;
import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumer;
import com.damdamdeo.todo.consumer.event.TodoAggregateTodoMarkedAsCompletedEventPayloadConsumer;
import com.damdamdeo.todo.infrastructure.JpaTodoRepository;
import com.damdamdeo.todo.infrastructure.TodoEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class TodoMarkedAsCompletedEventConsumer implements AggregateRootEventConsumer {

    private final Logger logger = LoggerFactory.getLogger(TodoMarkedAsCompletedEventConsumer.class);

    final JpaTodoRepository jpaTodoRepository;

    public TodoMarkedAsCompletedEventConsumer(final JpaTodoRepository jpaTodoRepository) {
        this.jpaTodoRepository = Objects.requireNonNull(jpaTodoRepository);
    }

    @Override
    public void consume(final AggregateRootEventConsumable aggregateRootEventConsumable) {
        logger.info(String.format("Consuming '%s' for '%s'", eventType(), aggregateRootEventConsumable.eventId()));
        final TodoAggregateTodoMarkedAsCompletedEventPayloadConsumer todoAggregateTodoMarkedAsCompletedEventPayloadConsumer = (TodoAggregateTodoMarkedAsCompletedEventPayloadConsumer) aggregateRootEventConsumable.eventPayload();
        final TodoEntity todoToMarkAsCompleted = jpaTodoRepository.find(
                todoAggregateTodoMarkedAsCompletedEventPayloadConsumer.todoId());
        todoToMarkAsCompleted.markAsCompleted(aggregateRootEventConsumable.eventId());
        jpaTodoRepository.merge(todoToMarkAsCompleted);
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
