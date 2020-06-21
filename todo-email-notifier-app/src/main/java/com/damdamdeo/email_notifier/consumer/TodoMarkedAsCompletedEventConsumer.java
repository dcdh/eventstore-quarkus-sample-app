package com.damdamdeo.email_notifier.consumer;

import com.damdamdeo.email_notifier.consumer.event.TodoAggregateTodoMarkedAsCompletedEventPayloadConsumer;
import com.damdamdeo.email_notifier.domain.EmailNotifier;
import com.damdamdeo.email_notifier.domain.TemplateGenerator;
import com.damdamdeo.email_notifier.infrastructure.TodoEntity;
import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumable;
import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumer;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class TodoMarkedAsCompletedEventConsumer implements AggregateRootEventConsumer {

    final TodoRepository todoRepository;
    final TemplateGenerator templateGenerator;
    final EmailNotifier emailNotifier;

    public TodoMarkedAsCompletedEventConsumer(final TodoRepository todoRepository,
                                              final TemplateGenerator templateGenerator,
                                              final EmailNotifier emailNotifier) {
        this.todoRepository = Objects.requireNonNull(todoRepository);
        this.templateGenerator = Objects.requireNonNull(templateGenerator);
        this.emailNotifier = Objects.requireNonNull(emailNotifier);
    }

    @Override
    public void consume(final AggregateRootEventConsumable aggregateRootEventConsumable) {
        final TodoAggregateTodoMarkedAsCompletedEventPayloadConsumer todoAggregateTodoMarkedAsCompletedEventPayloadConsumer = (TodoAggregateTodoMarkedAsCompletedEventPayloadConsumer) aggregateRootEventConsumable.eventPayload();
        try {
            final TodoEntity todoToMarkAsCompleted = todoRepository.find(todoAggregateTodoMarkedAsCompletedEventPayloadConsumer.todoId());
            todoToMarkAsCompleted.markAsCompleted(aggregateRootEventConsumable.eventId());
            todoRepository.merge(todoToMarkAsCompleted);
            final String content = templateGenerator.generate(new DefaultTodoMarkedAsCompleted(todoToMarkAsCompleted.todoId(),
                    todoToMarkAsCompleted.description()));
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
