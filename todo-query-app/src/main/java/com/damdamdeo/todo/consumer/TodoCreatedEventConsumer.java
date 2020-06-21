package com.damdamdeo.todo.consumer;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumable;
import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumer;
import com.damdamdeo.todo.consumer.event.TodoAggregateTodoCreatedEventPayloadConsumer;
import com.damdamdeo.todo.domain.api.TodoStatus;
import com.damdamdeo.todo.infrastructure.JpaTodoRepository;
import com.damdamdeo.todo.infrastructure.TodoEntity;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class TodoCreatedEventConsumer implements AggregateRootEventConsumer {

    final JpaTodoRepository jpaTodoRepository;

    public TodoCreatedEventConsumer(final JpaTodoRepository jpaTodoRepository) {
        this.jpaTodoRepository = Objects.requireNonNull(jpaTodoRepository);
    }

    @Override
    public void consume(final AggregateRootEventConsumable aggregateRootEventConsumable) {
        final TodoAggregateTodoCreatedEventPayloadConsumer todoAggregateTodoCreatedEventPayloadConsumer = (TodoAggregateTodoCreatedEventPayloadConsumer) aggregateRootEventConsumable.eventPayload();
        final TodoEntity todoToCreate = new TodoEntity(
                todoAggregateTodoCreatedEventPayloadConsumer.todoId(),
                todoAggregateTodoCreatedEventPayloadConsumer.description(),
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
