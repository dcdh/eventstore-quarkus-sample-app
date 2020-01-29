package com.damdamdeo.todo.interfaces;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.Event;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumer;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventQualifier;
import com.damdamdeo.todo.aggregate.event.TodoAggregateTodoMarkedAsCompletedEventPayload;
import com.damdamdeo.todo.infrastructure.TodoEntity;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import java.util.Objects;

@Dependent
@EventQualifier(aggregateRootType = "TodoAggregateRoot", eventType = "TodoMarkedAsCompletedEvent")
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
        todoToMarkAsCompleted.markAsCompleted(event.eventId(),
                event.version());
        entityManager.merge(todoToMarkAsCompleted);
    }

}
