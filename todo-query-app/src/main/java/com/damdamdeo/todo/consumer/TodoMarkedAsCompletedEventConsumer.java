package com.damdamdeo.todo.consumer;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumer;
import com.damdamdeo.eventdataspreader.event.api.Event;
import com.damdamdeo.todo.domain.api.event.TodoAggregateTodoMarkedAsCompletedEventPayload;
import com.damdamdeo.todo.infrastructure.TodoEntity;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import java.util.Objects;

@Dependent
public class TodoMarkedAsCompletedEventConsumer implements EventConsumer {

    final EntityManager entityManager;

    public TodoMarkedAsCompletedEventConsumer(final EntityManager entityManager) {
        this.entityManager = Objects.requireNonNull(entityManager);
    }

    @Override
    public void consume(final Event event) {
        final TodoAggregateTodoMarkedAsCompletedEventPayload todoAggregateTodoMarkedAsCompletedEventPayload = (TodoAggregateTodoMarkedAsCompletedEventPayload) event.eventPayload();
        final TodoEntity todoToMarkAsCompleted = entityManager.find(TodoEntity.class,
                todoAggregateTodoMarkedAsCompletedEventPayload.todoId());
        todoToMarkAsCompleted.markAsCompleted(event.eventId());
        entityManager.merge(todoToMarkAsCompleted);
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
