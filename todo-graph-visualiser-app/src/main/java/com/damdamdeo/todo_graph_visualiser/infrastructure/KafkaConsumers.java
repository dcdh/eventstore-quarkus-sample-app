package com.damdamdeo.todo_graph_visualiser.infrastructure;

import io.smallrye.reactive.messaging.kafka.ReceivedKafkaMessage;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import io.vertx.core.json.JsonObject;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@ApplicationScoped
public class KafkaConsumers {

    @Inject
    Neo4JRepository neo4JRepository;

    // use only one executor to ensure that only one message will be handled between multiple topics
    // the neo4j repository will handle the disorder of message handling between event and aggregaterootprojection topics
    private final Executor executor = Executors.newSingleThreadExecutor();

    private static final String AFTER = "after";

    @Incoming("event")
    public CompletionStage<Void> onEvent(final ReceivedKafkaMessage<JsonObject, JsonObject> message) {
        return CompletableFuture.supplyAsync(() -> {
            final JsonObject after = message.getPayload().getJsonObject(AFTER);
            final String eventType = after.getString("eventtype");
            switch (eventType) {
                case "TodoCreatedEvent":
                    neo4JRepository.persistTodoCreatedEvent(
                            after.getString("eventid"),
                            after.getString("aggregaterootid"),
                            after.getLong("creationdate"),
                            new JsonObject(after.getString("metadata")),
                            new JsonObject(after.getString("eventpayload")),
                            after.getLong("version"));
                    break;
                case "TodoMarkedAsCompletedEvent":
                    neo4JRepository.persistTodoMarkedAsCompletedEvent(
                            after.getString("eventid"),
                            after.getString("aggregaterootid"),
                            after.getLong("creationdate"),
                            new JsonObject(after.getString("metadata")),
                            new JsonObject(after.getString("eventpayload")),
                            after.getLong("version"));
                    break;
                default:
                    // TODO log
                    break;
            }
            return null;
        }, executor);
    }

    @Incoming("aggregaterootprojection")
    public CompletionStage<Void> onAggregateRootProjection(final ReceivedKafkaMessage<JsonObject, JsonObject> message) {
        return CompletableFuture.supplyAsync(() -> {
            final JsonObject after = message.getPayload().getJsonObject(AFTER);
            neo4JRepository.persistTodoAggregate(
                    after.getString("aggregaterootid"),
                    new JsonObject(after.getString("aggregateroot")),
                    after.getLong("version"));
            return null;
        }, executor);
    }

}
