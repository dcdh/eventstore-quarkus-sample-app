package com.damdamdeo.todo.consumer;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumer;
import com.damdamdeo.eventdataspreader.event.api.Event;
import com.damdamdeo.todo.domain.api.TodoStatus;
import com.damdamdeo.todo.domain.api.event.TodoAggregateTodoCreatedEventPayload;
import com.damdamdeo.todo.infrastructure.TodoEntity;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import java.util.Objects;

@Dependent
public class TodoCreatedEventConsumer implements EventConsumer {

    final EntityManager entityManager;

    public TodoCreatedEventConsumer(final EntityManager entityManager) {
        this.entityManager = Objects.requireNonNull(entityManager);
    }

    @Override
    public void consume(final Event event) {
        final TodoAggregateTodoCreatedEventPayload todoAggregateTodoCreatedEventPayload = (TodoAggregateTodoCreatedEventPayload) event.eventPayload();
        final TodoEntity todoToCreate = new TodoEntity(
                todoAggregateTodoCreatedEventPayload.todoId(),
                todoAggregateTodoCreatedEventPayload.description(),
                TodoStatus.IN_PROGRESS,
                event.eventId());
        entityManager.persist(todoToCreate);
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
