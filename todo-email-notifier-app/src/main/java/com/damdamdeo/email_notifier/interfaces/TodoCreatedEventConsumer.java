package com.damdamdeo.email_notifier.interfaces;

import com.damdamdeo.email_notifier.domain.EmailNotifier;
import com.damdamdeo.email_notifier.domain.TemplateGenerator;
import com.damdamdeo.email_notifier.domain.TodoCreated;
import com.damdamdeo.email_notifier.domain.TodoStatus;
import com.damdamdeo.email_notifier.infrastructure.TodoEntity;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.Event;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumer;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventQualifier;
import com.damdamdeo.todo.domain.api.event.TodoAggregateTodoCreatedEventPayload;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import java.util.Objects;

@Dependent
@EventQualifier(aggregateRootType = "TodoAggregateRoot", eventType = "TodoCreatedEvent")
public class TodoCreatedEventConsumer implements EventConsumer {

    final EntityManager entityManager;
    final TemplateGenerator templateGenerator;
    final EmailNotifier emailNotifier;

    public TodoCreatedEventConsumer(final EntityManager entityManager,
                                    final TemplateGenerator templateGenerator,
                                    final EmailNotifier emailNotifier) {
        this.entityManager = Objects.requireNonNull(entityManager);
        this.templateGenerator = Objects.requireNonNull(templateGenerator);
        this.emailNotifier = Objects.requireNonNull(emailNotifier);
    }

    @Override
    public void consume(final Event event) {
        final TodoAggregateTodoCreatedEventPayload todoAggregateTodoCreatedEventPayload = (TodoAggregateTodoCreatedEventPayload) event.eventPayload();
        try {
            final TodoEntity todoToCreate = new TodoEntity(
                    todoAggregateTodoCreatedEventPayload.todoId(),
                    todoAggregateTodoCreatedEventPayload.description(),
                    TodoStatus.IN_PROGRESS,
                    event.eventId());
            entityManager.persist(todoToCreate);
            final String content = templateGenerator.generate(new TodoCreated() {
                @Override
                public String todoId() {
                    return todoToCreate.todoId();
                }

                @Override
                public String description() {
                    return todoToCreate.description();
                }
            });
            emailNotifier.notify("New Todo created", content).toCompletableFuture().get();
        } catch (Exception e) {
            // TODO log
            throw new RuntimeException(e);
        }
    }

}
