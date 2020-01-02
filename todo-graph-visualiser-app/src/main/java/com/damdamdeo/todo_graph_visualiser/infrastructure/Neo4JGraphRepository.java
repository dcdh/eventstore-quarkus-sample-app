package com.damdamdeo.todo_graph_visualiser.infrastructure;

import com.damdamdeo.todo_graph_visualiser.domain.GraphRepository;
import com.damdamdeo.todo_graph_visualiser.domain.Todo;
import io.vertx.core.json.JsonObject;
import org.neo4j.driver.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class Neo4JGraphRepository implements GraphRepository {

    @Inject
    Driver driver;

    @Override
    public void persistTodoCreatedEvent(final String eventId,
                                        final String aggregateId,
                                        final Long creationDate,
                                        final JsonObject metadata,
                                        final JsonObject eventPayload,
                                        final Long version) {
        final Session session = driver.session();
        final String aggregateRootType = "TodoAggregateRoot";
        final String eventType = "TodoCreatedEvent";
        final String query = String.format("MERGE (n:%s { aggregateId: $aggregateId}) CREATE (n)-[r:%s { eventId: $eventId, eventType: $eventType, creationDate: $creationDate, version: $version, todoId: $todoId, description: $description }]->(n)",
                aggregateRootType, eventType);
        session.writeTransaction(tx -> tx.run(query,
                Values.parameters("aggregateId", aggregateId,
                        "eventId", eventId,
                        "eventType", eventType,
                        "creationDate", creationDate,
                        "version", version,
                        "todoId", eventPayload.getString("todoId"),
                        "description", eventPayload.getString("description"))));
    }

    @Override
    public void persistTodoMarkedAsCompletedEvent(final String eventId,
                                                  final String aggregateId,
                                                  final Long creationDate,
                                                  final JsonObject metadata,
                                                  final JsonObject eventPayload,
                                                  final Long version) {
        final Session session = driver.session();
        final String eventType = "TodoMarkedAsCompletedEvent";
        final String query = String.format("MATCH (n:TodoAggregateRoot { aggregateId: $aggregateId}) CREATE (n)-[r:%s { eventId: $eventId, eventType: $eventType, creationDate: $creationDate, version: $version, todoId: $todoId }]->(n)",
                eventType);
        session.writeTransaction(tx -> tx.run(query,
                Values.parameters("aggregateId", aggregateId,
                        "eventId", eventId,
                        "eventType", eventType,
                        "creationDate", creationDate,
                        "version", version,
                        "todoId", eventPayload.getString("todoId"))));
    }

    @Override
    public void persistTodoAggregate(final String aggregateId,
                                     final JsonObject aggregateRoot,
                                     final Long version) {
        final Session session = driver.session();
        session.writeTransaction(tx -> tx.run("MERGE (n:TodoAggregateRoot { aggregateId: $aggregateId }) " +
                        "ON CREATE SET n.version = $version, n.description = $description, n.todoStatus = $todoStatus " +
                        "ON MATCH SET n.version = $version, n.description = $description, n.todoStatus = $todoStatus",
                Values.parameters("aggregateId", aggregateId,
                        "version", version,
                        "description", aggregateRoot.getString("description"),
                        "todoStatus", aggregateRoot.getString("todoStatus"))));
    }

    @Override
    public List<Todo> getAll() {
        final Session session = driver.session();
        return session.readTransaction(tx ->
            tx.run("MATCH (todo)-[events]-(todo) WITH todo, events ORDER BY events.version ASC  RETURN todo {.*}, collect(events) AS events")
                    .stream()
                    .map(record -> {
                            final Value todo = record.get("todo");
                            final Value events = record.get("events");
                            return new Todo(
                                    todo.get("aggregateId").asString(),
                                    todo.get("description").asString(null),
                                    todo.get("todoStatus").asString(),
                                    todo.get("version").asInt(),
                                    events.asList(value -> {
                                        final Map<String, Object> event = new HashMap<>();
                                        event.put("eventType", value.get("eventType").asString());
                                        event.put("eventId", value.get("eventId").asString());
                                        event.put("version", value.get("version").asInt());
                                        event.put("creationDate", value.get("creationDate").asLong());
                                        event.put("todoId", value.get("todoId").asString());
                                        event.put("description", value.get("description").asString(null));
                                        return event;
                                    })
                            );
                    }).collect(Collectors.toList())
        );
    }

}
