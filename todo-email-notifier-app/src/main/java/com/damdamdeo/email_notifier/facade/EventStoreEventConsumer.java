package com.damdamdeo.email_notifier.facade;

import com.damdamdeo.email_notifier.domain.*;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;
import io.vertx.core.json.JsonObject;
import org.apache.kafka.common.header.Header;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class EventStoreEventConsumer {

    private final static Logger LOGGER = Logger.getLogger(EventStoreEventConsumer.class.getName());

    final EntityManager em;
    final EventStoreRepository eventStoreRepository;
    final TemplateGenerator templateGenerator;
    final EmailNotifier emailNotifier;

    public EventStoreEventConsumer(final EntityManager em,
                                   final EventStoreRepository eventStoreRepository,
                                   final TemplateGenerator templateGenerator,
                                   final EmailNotifier emailNotifier) {
        this.em = Objects.requireNonNull(em);
        this.eventStoreRepository = Objects.requireNonNull(eventStoreRepository);
        this.templateGenerator = Objects.requireNonNull(templateGenerator);
        this.emailNotifier = Objects.requireNonNull(emailNotifier);
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
                            final String eventPayload = after.getString("payload");
                            final Long version = after.getLong("version");
                            switch (eventType) {
                                case "TodoCreatedEvent":
                                    final JsonObject todoCreatedEvent = new JsonObject(eventPayload);
                                    new TodoCreatedEvent(todoCreatedEvent).handle(version, em, templateGenerator, emailNotifier);
                                    break;
                                case "TodoMarkedAsCompletedEvent":
                                    final JsonObject todoMarkedAsCompletedEvent = new JsonObject(eventPayload);
                                    new TodoMarkedAsCompletedEvent(todoMarkedAsCompletedEvent).handle(version, em, templateGenerator, emailNotifier);
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
