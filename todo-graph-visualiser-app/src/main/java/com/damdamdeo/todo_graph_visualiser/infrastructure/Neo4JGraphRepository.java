package com.damdamdeo.todo_graph_visualiser.infrastructure;

import com.damdamdeo.todo_graph_visualiser.domain.Graph;
import com.damdamdeo.todo_graph_visualiser.domain.GraphRepository;
import com.damdamdeo.todo_graph_visualiser.domain.Todo;
import io.vertx.core.json.JsonObject;
import org.neo4j.driver.*;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Graph getGraph() {
        final Session session = driver.session();
        final List<Todo> todos = new ArrayList<>();
        final List<Map<String, Object>> events = new ArrayList<>();
        return session.readTransaction(tx -> {
            tx.run("MATCH (sourceTodo)-[events]-(targetTodo) WITH sourceTodo, targetTodo, events ORDER BY events.version ASC RETURN sourceTodo {.*}, targetTodo {.*}, collect(events) AS events")
                    .stream()
                    .forEach(record -> {
                        final Value sourceTodoRecord = record.get("sourceTodo");
                        final Value targetTodoTodoRecord = record.get("targetTodo");
                        final Value eventsRecord = record.get("events");
                        todos.add(new Todo(
                                sourceTodoRecord.get("aggregateId").asString(),
                                sourceTodoRecord.get("description").asString(null),
                                sourceTodoRecord.get("todoStatus").asString(),
                                sourceTodoRecord.get("version").asInt()));
                        events.addAll(eventsRecord.asList(eventRecord -> {
                            final Map<String, Object> event = new HashMap<>();
                            event.put("source", sourceTodoRecord.get("aggregateId").asString());
                            event.put("target", targetTodoTodoRecord.get("aggregateId").asString());
                            event.put("eventType", eventRecord.get("eventType").asString());
                            event.put("eventId", eventRecord.get("eventId").asString());
                            event.put("version", eventRecord.get("version").asInt());
                            event.put("creationDate", eventRecord.get("creationDate").asLong());
                            event.put("todoId", eventRecord.get("todoId").asString());
                            event.put("description", eventRecord.get("description").asString(null));
                            return event;
                        }));
                    });
            return new Graph(todos, events);
        });
    }

}
