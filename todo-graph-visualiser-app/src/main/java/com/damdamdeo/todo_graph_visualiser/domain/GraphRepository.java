package com.damdamdeo.todo_graph_visualiser.domain;

import io.vertx.core.json.JsonObject;

import java.util.List;

public interface GraphRepository {

    void persistTodoCreatedEvent(String eventId, String aggregateId, Long creationDate, JsonObject metadata, JsonObject eventPayload,
                                 Long version);

    void persistTodoMarkedAsCompletedEvent(String eventId, String aggregateId, Long creationDate, JsonObject metadata, JsonObject eventPayload,
                                           Long version);

    void persistTodoAggregate(String aggregateId, JsonObject aggregateRoot, Long version);

    List<Todo> getAll();

}
