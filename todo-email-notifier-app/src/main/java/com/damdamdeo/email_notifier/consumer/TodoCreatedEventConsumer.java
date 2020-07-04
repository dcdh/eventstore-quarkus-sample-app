package com.damdamdeo.email_notifier.consumer;

import com.damdamdeo.email_notifier.domain.EmailNotifier;
import com.damdamdeo.email_notifier.domain.TemplateGenerator;
import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumable;
import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;

@ApplicationScoped
public class TodoCreatedEventConsumer implements AggregateRootEventConsumer {

    private final Logger logger = LoggerFactory.getLogger(TodoCreatedEventConsumer.class);

    final TemplateGenerator templateGenerator;
    final EmailNotifier emailNotifier;

    public TodoCreatedEventConsumer(final TemplateGenerator templateGenerator,
                                    final EmailNotifier emailNotifier) {
        this.templateGenerator = Objects.requireNonNull(templateGenerator);
        this.emailNotifier = Objects.requireNonNull(emailNotifier);
    }

    @Override
    public void consume(final AggregateRootEventConsumable aggregateRootEventConsumable) {
        logger.info(String.format("Consuming '%s' for '%s'", eventType(), aggregateRootEventConsumable.eventId()));
        final TodoAggregateRootMaterializedStateConsumer todoAggregateRootMaterializedStateConsumer = (TodoAggregateRootMaterializedStateConsumer) aggregateRootEventConsumable.materializedState();
        try {
            final String content = templateGenerator.generateTodoCreated(todoAggregateRootMaterializedStateConsumer.toDomain());
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
