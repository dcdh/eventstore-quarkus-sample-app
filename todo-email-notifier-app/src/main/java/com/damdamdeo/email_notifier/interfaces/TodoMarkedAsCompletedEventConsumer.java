package com.damdamdeo.email_notifier.interfaces;

import com.damdamdeo.email_notifier.domain.EmailNotifier;
import com.damdamdeo.email_notifier.domain.TemplateGenerator;
import com.damdamdeo.email_notifier.domain.TodoMarkedAsCompleted;
import com.damdamdeo.email_notifier.infrastructure.TodoEntity;
import com.damdamdeo.eventdataspreader.debeziumeventconsumer.api.EventConsumer;
import com.damdamdeo.eventdataspreader.event.api.Event;
import com.damdamdeo.todo.domain.api.event.TodoAggregateTodoMarkedAsCompletedEventPayload;

import javax.enterprise.context.Dependent;
import javax.persistence.EntityManager;
import java.util.Objects;

@Dependent
public class TodoMarkedAsCompletedEventConsumer implements EventConsumer {

    final EntityManager entityManager;
    final TemplateGenerator templateGenerator;
    final EmailNotifier emailNotifier;

    public TodoMarkedAsCompletedEventConsumer(final EntityManager entityManager,
                                              final TemplateGenerator templateGenerator,
                                              final EmailNotifier emailNotifier) {
        this.entityManager = Objects.requireNonNull(entityManager);
        this.templateGenerator = Objects.requireNonNull(templateGenerator);
        this.emailNotifier = Objects.requireNonNull(emailNotifier);
    }

    @Override
    public void consume(final Event event) {
        final TodoAggregateTodoMarkedAsCompletedEventPayload todoAggregateTodoMarkedAsCompletedEventPayload = (TodoAggregateTodoMarkedAsCompletedEventPayload) event.eventPayload();
        try {
            final TodoEntity todoToMarkAsCompleted = entityManager.find(TodoEntity.class,
                    todoAggregateTodoMarkedAsCompletedEventPayload.todoId());
            todoToMarkAsCompleted.markAsCompleted(event.eventId());
            entityManager.merge(todoToMarkAsCompleted);
            final String content = templateGenerator.generate(new TodoMarkedAsCompleted() {
                @Override
                public String todoId() {
                    return todoToMarkAsCompleted.todoId();
                }

                @Override
                public String description() {
                    return todoToMarkAsCompleted.description();
                }
            });
            emailNotifier.notify("Todo marked as completed", content);
        } catch (Exception e) {
            // TODO log
            throw new RuntimeException(e);
        }
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
