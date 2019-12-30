package com.damdamdeo.todo_graph_visualiser.infrastructure;

import io.vertx.core.json.JsonObject;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Values;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class Neo4JRepository {

    @Inject
    Driver driver;

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

}
