package com.damdamdeo.todo.facade;

import com.damdamdeo.todo.domain.EventStoreRepository;
import com.damdamdeo.todo.domain.TodoStatus;
import com.damdamdeo.todo.infrastructure.TodoEntity;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import org.apache.kafka.common.header.Header;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import io.vertx.core.json.JsonObject;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class EventStoreEventConsumer {

    private final static Logger LOGGER = Logger.getLogger(EventStoreEventConsumer.class.getName());

    final EventStoreRepository eventStoreRepository;

    final EntityManager em;

    public EventStoreEventConsumer(final EventStoreRepository eventStoreRepository,
                                   final EntityManager em) {
        this.eventStoreRepository = Objects.requireNonNull(eventStoreRepository);
        this.em = Objects.requireNonNull(em);
    }

    private static final class NotAnEventPayloadException extends RuntimeException {

    }

    @Incoming("event")
    @Transactional
    public CompletionStage<Void> onMessage(final KafkaMessage<JsonObject, JsonObject> message) throws IOException {
        try {
            final UUID eventId = Optional.of(message.getKey().getJsonObject("payload").getString("eventid"))
                    .map(UUID::fromString)
                    .orElseThrow(() -> new NotAnEventPayloadException());
            if (!eventStoreRepository.hasConsumedEvent(eventId)) {
                final JsonObject payload = message.getPayload().getJsonObject("payload");
                if (payload != null) {
                    final JsonObject after = payload.getJsonObject("after");
                    if (after != null) {
                        final String aggregateroottype = after.getString("aggregateroottype");
                        if ("TodoAggregateRoot".equals(aggregateroottype)) {
                            final String eventType = after.getString("eventtype");
                            final String eventid = after.getString("eventid");
                            final String eventPayload = after.getString("payload");
                            final Long version = after.getLong("version");
                            switch (eventType) {
                                // /!\ prendre exemple sur l'email notifier qui est codé d'une façon plus agréable (method handler !)
                                case "TodoCreatedEvent":
                                    final JsonObject todoCreatedEvent = new JsonObject(eventPayload);
                                    final TodoEntity todoToCreate = new TodoEntity(
                                            todoCreatedEvent.getString("todoId"),
                                            todoCreatedEvent.getString("description"),
                                            TodoStatus.IN_PROGRESS,
                                            version);
                                    em.merge(todoToCreate);
                                    break;
                                case "TodoMarkedAsCompletedEvent":
                                    final JsonObject todoMarkedAsCompletedEvent = new JsonObject(eventPayload);
                                    final TodoEntity todoToMarkAsCompleted = em.find(TodoEntity.class, todoMarkedAsCompletedEvent.getString("todoId"));
                                    todoToMarkAsCompleted.markAsCompleted(version);
                                    em.merge(todoToMarkAsCompleted);
                                    break;
                                default:
                                    LOGGER.log(Level.INFO, String.format("eventType '%s' not supported for eventId '%s'", eventType, eventId));
                                    break;
                            }
                        } else {
                            LOGGER.log(Level.INFO, String.format("Unsupported aggregate root type '%s' for eventId '%s'", aggregateroottype, eventId));
                        }
                    } else {
                        LOGGER.log(Level.INFO, String.format("Missing 'after' for eventId '%s'", eventId));
                    }
                } else {
                    LOGGER.log(Level.INFO, String.format("Missing 'payload' for eventId '%s'", eventId));
                }
                final Map<String, List<String>> headers = new HashMap<>();
                for (final Header header : message.getHeaders().unwrap()) {
                    headers.put(header.key(), message.getHeaders().getAllAsStrings(header.key()));
                }
                eventStoreRepository.markEventAsConsumed(eventId,
                        new Date());
            } else {
                LOGGER.log(Level.INFO, String.format("Event '%s' already consumed", eventId));
            }
        } catch (final NotAnEventPayloadException notAnEventPayloadException) {
            LOGGER.log(Level.WARNING, String.format("Message not an event !"));// TODO better login
        }
        return message.ack();
    }

}
