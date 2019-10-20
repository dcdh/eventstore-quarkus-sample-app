package com.damdamdeo.todo.interfaces;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.Event;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumer;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventQualifier;
import com.damdamdeo.todo.domain.TodoStatus;
import com.damdamdeo.todo.infrastructure.TodoEntity;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import java.util.Objects;

@Dependent
@EventQualifier(aggregateRootType = "TodoAggregateRoot", eventType = "TodoCreatedEvent")
public class TodoCreatedEventConsumer implements EventConsumer {

    final EntityManager entityManager;

    public TodoCreatedEventConsumer(final EntityManager entityManager) {
        this.entityManager = Objects.requireNonNull(entityManager);
    }

    @Override
    public void consume(final Event event) {
        final TodoEntity todoToCreate = new TodoEntity(
                event.payload().getString("todoId"),
                event.payload().getString("description"),
                TodoStatus.IN_PROGRESS,
                event.eventId().toString(),
                event.version());
        entityManager.persist(todoToCreate);
    }

}
