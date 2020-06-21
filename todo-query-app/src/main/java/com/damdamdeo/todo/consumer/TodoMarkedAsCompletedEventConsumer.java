package com.damdamdeo.todo.consumer;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumable;
import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumer;
import com.damdamdeo.todo.consumer.event.TodoAggregateTodoMarkedAsCompletedEventPayloadConsumer;
import com.damdamdeo.todo.infrastructure.JpaTodoRepository;
import com.damdamdeo.todo.infrastructure.TodoEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class TodoMarkedAsCompletedEventConsumer implements AggregateRootEventConsumer {

    final JpaTodoRepository jpaTodoRepository;

    public TodoMarkedAsCompletedEventConsumer(final JpaTodoRepository jpaTodoRepository) {
        this.jpaTodoRepository = Objects.requireNonNull(jpaTodoRepository);
    }

    @Override
    public void consume(final AggregateRootEventConsumable aggregateRootEventConsumable) {
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
