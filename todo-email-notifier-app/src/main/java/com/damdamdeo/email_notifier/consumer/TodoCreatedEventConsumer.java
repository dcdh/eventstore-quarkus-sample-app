package com.damdamdeo.email_notifier.consumer;

import com.damdamdeo.email_notifier.consumer.event.TodoAggregateTodoCreatedEventPayloadConsumer;
import com.damdamdeo.email_notifier.domain.EmailNotifier;
import com.damdamdeo.email_notifier.domain.TemplateGenerator;
import com.damdamdeo.email_notifier.domain.TodoStatus;
import com.damdamdeo.email_notifier.infrastructure.TodoEntity;
import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumable;
import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumer;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class TodoCreatedEventConsumer implements AggregateRootEventConsumer {

    final TodoRepository todoRepository;
    final TemplateGenerator templateGenerator;
    final EmailNotifier emailNotifier;

    public TodoCreatedEventConsumer(final TodoRepository todoRepository,
                                    final TemplateGenerator templateGenerator,
                                    final EmailNotifier emailNotifier) {
        this.todoRepository = Objects.requireNonNull(todoRepository);
        this.templateGenerator = Objects.requireNonNull(templateGenerator);
        this.emailNotifier = Objects.requireNonNull(emailNotifier);
    }

    @Override
    public void consume(final AggregateRootEventConsumable aggregateRootEventConsumable) {
        final TodoAggregateTodoCreatedEventPayloadConsumer todoAggregateTodoCreatedEventPayloadConsumer = (TodoAggregateTodoCreatedEventPayloadConsumer) aggregateRootEventConsumable.eventPayload();
        try {
            final TodoEntity todoToCreate = new TodoEntity(
                    todoAggregateTodoCreatedEventPayloadConsumer.todoId(),
                    todoAggregateTodoCreatedEventPayloadConsumer.description(),
                    TodoStatus.IN_PROGRESS,
                    aggregateRootEventConsumable.eventId());
            todoRepository.persist(todoToCreate);
            final String content = templateGenerator.generate(new DefaultTodoCreated(todoToCreate.todoId(), todoToCreate.description()));
            emailNotifier.notify("New Todo created", content);
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
        return "TodoCreatedEvent";
    }

}
