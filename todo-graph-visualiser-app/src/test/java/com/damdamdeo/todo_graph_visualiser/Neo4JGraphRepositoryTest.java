package com.damdamdeo.todo_graph_visualiser;

import com.damdamdeo.todo_graph_visualiser.domain.Graph;
import com.damdamdeo.todo_graph_visualiser.domain.Todo;
import io.quarkus.test.junit.QuarkusTest;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import java.util.*;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class Neo4JGraphRepositoryTest extends CommonTest {

    @Test
    public void should_create_expected_relationship_on_TodoCreatedEvent() {
        // Given
        // When
        persistTodoCreatedEvent();

        // Then
        given()
                .auth()
                .basic(username, password)
                .body("{\n" +
                        "  \"statements\" : [ {\n" +
                        "    \"statement\" : \"MATCH (n)-[r]-(m) RETURN r\"\n" +
                        "  } ]\n" +
                        "}")
                .accept("application/json; charset=UTF-8 ")
                .contentType("application/json")
                .when()
                .post("http://localhost:7474/db/data/transaction/commit")
                .then()
                .log()
                .all()
                .body("results.size()", CoreMatchers.equalTo(1))
                .body("results[0].data[0].row[0].description", CoreMatchers.equalTo("lorem ipsum"))
                .body("results[0].data[0].row[0].eventType", CoreMatchers.equalTo("TodoCreatedEvent"))
                .body("results[0].data[0].row[0].todoId", CoreMatchers.equalTo("todoId"))
                .body("results[0].data[0].row[0].creationDate", CoreMatchers.equalTo(1562890044742000L))
                .body("results[0].data[0].row[0].version", CoreMatchers.equalTo(0))
                .statusCode(200);
    }

    @Test
    public void should_create_expected_relationship_on_TodoMarkedAsCompletedEvent() {
        // Given
        persistTodoCreatedEvent();

        // When
        persistTodoMarkedAsCompletedEvent();

        // Then
        given()
                .auth()
                .basic(username, password)
                .body("{\n" +
                        "  \"statements\" : [ {\n" +
                        "    \"statement\" : \"MATCH (n)-[r]-(m) RETURN r\"\n" +
                        "  } ]\n" +
                        "}")
                .accept("application/json; charset=UTF-8 ")
                .contentType("application/json")
                .when()
                .post("http://localhost:7474/db/data/transaction/commit")
                .then()
                .log()
                .all()
                .body("results.size()", CoreMatchers.equalTo(1))
                .body("results[0].data[0].row[0].eventType", CoreMatchers.equalTo("TodoMarkedAsCompletedEvent"))
                .body("results[0].data[0].row[0].todoId", CoreMatchers.equalTo("todoId"))
                .body("results[0].data[0].row[0].creationDate", CoreMatchers.equalTo(1562890044922000L))
                .body("results[0].data[0].row[0].version", CoreMatchers.equalTo(1))
                .statusCode(200);
    }

    @Test
    public void should_create_node_when_persist_TodoCreatedEvent_on_un_existent_node() {
        // Given
        // When
        persistTodoCreatedEvent();

        // Then
        given()
                .auth()
                .basic(username, password)
                .body("{\n" +
                        "  \"statements\" : [ {\n" +
                        "    \"statement\" : \"MATCH (n:TodoAggregateRoot { aggregateId: 'todoId' }) RETURN n\"\n" +
                        "  } ]\n" +
                        "}")
                .accept("application/json; charset=UTF-8 ")
                .contentType("application/json")
                .when()
                .post("http://localhost:7474/db/data/transaction/commit")
                .then()
                .log()
                .all()
                .body("results.size()", CoreMatchers.equalTo(1))
                .body("results[0].data[0].row[0].aggregateId", CoreMatchers.equalTo("todoId"))
                .statusCode(200);
    }

    @Test
    public void should_persist_aggregateRoot() {
        // Given
        // When
        persistTodoAggregate();

        // Then
        given()
                .auth()
                .basic(username, password)
                .body("{\n" +
                        "  \"statements\" : [ {\n" +
                        "    \"statement\" : \"MATCH (n:TodoAggregateRoot { aggregateId: 'todoId' }) RETURN n\"\n" +
                        "  } ]\n" +
                        "}")
                .accept("application/json; charset=UTF-8 ")
                .contentType("application/json")
                .when()
                .post("http://localhost:7474/db/data/transaction/commit")
                .then()
                .log()
                .all()
                .body("results.size()", CoreMatchers.equalTo(1))
                .body("results[0].data[0].row[0].aggregateId", CoreMatchers.equalTo("todoId"))
                .body("results[0].data[0].row[0].todoStatus", CoreMatchers.equalTo("COMPLETED"))
                .body("results[0].data[0].row[0].description", CoreMatchers.equalTo("lorem ipsum"))
                .body("results[0].data[0].row[0].version", CoreMatchers.equalTo(1))
                .statusCode(200);
    }

    @Test
    public void should_persist_event_and_aggregate() {
        // Given
        persistTodoCreatedEvent();
        persistTodoAggregate();

        // When && Then
        given()
                .auth()
                .basic(username, password)
                .body("{\n" +
                        "  \"statements\" : [ {\n" +
                        "    \"statement\" : \"MATCH p =(a { aggregateId: 'todoId' })-[r]->(b) RETURN a,r\"\n" +
                        "  } ]\n" +
                        "}")
                .accept("application/json; charset=UTF-8 ")
                .contentType("application/json")
                .when()
                .post("http://localhost:7474/db/data/transaction/commit")
                .then()
                .log()
                .all()
                .body("results.size()", CoreMatchers.equalTo(1))
                .body("results[0].data[0].row[0].aggregateId", CoreMatchers.equalTo("todoId"))
                .body("results[0].data[0].row[0].todoStatus", CoreMatchers.equalTo("COMPLETED"))
                .body("results[0].data[0].row[0].description", CoreMatchers.equalTo("lorem ipsum"))
                .body("results[0].data[0].row[0].version", CoreMatchers.equalTo(1))
                .body("results[0].data[0].row[1].description", CoreMatchers.equalTo("lorem ipsum"))
                .body("results[0].data[0].row[1].eventType", CoreMatchers.equalTo("TodoCreatedEvent"))
                .body("results[0].data[0].row[1].todoId", CoreMatchers.equalTo("todoId"))
                .body("results[0].data[0].row[1].creationDate", CoreMatchers.equalTo(1562890044742000L))
                .body("results[0].data[0].row[1].version", CoreMatchers.equalTo(0))
                .statusCode(200);
    }

    @Test
    public void should_getAll() {
        // Given
        persistTodoCreatedEvent();
        persistTodoMarkedAsCompletedEvent();
        persistTodoAggregate();

        // When
        final Graph graph = neo4JGraphRepository.getGraph();

        // Then
        final List<Map<String, Object>> expectedEventsForTodo = new ArrayList<>();
        final Map<String, Object> todoCreatedEvent = new HashMap<>();
        todoCreatedEvent.put("source", "todoId");
        todoCreatedEvent.put("target", "todoId");
        todoCreatedEvent.put("description", "lorem ipsum");
        todoCreatedEvent.put("eventType", "TodoCreatedEvent");
        todoCreatedEvent.put("todoId", "todoId");
        todoCreatedEvent.put("creationDate", 1562890044742000L);
        todoCreatedEvent.put("version", 0);

        final Map<String, Object> todoMarkedAsCompletedEvent = new HashMap<>();
        todoMarkedAsCompletedEvent.put("source", "todoId");
        todoMarkedAsCompletedEvent.put("target", "todoId");
        todoMarkedAsCompletedEvent.put("description", null);
        todoMarkedAsCompletedEvent.put("eventType", "TodoMarkedAsCompletedEvent");
        todoMarkedAsCompletedEvent.put("todoId", "todoId");
        todoMarkedAsCompletedEvent.put("creationDate", 1562890044922000L);
        todoMarkedAsCompletedEvent.put("version", 1);

        expectedEventsForTodo.add(todoCreatedEvent);
        expectedEventsForTodo.add(todoMarkedAsCompletedEvent);
        final Graph expectedGraph = new Graph(
                Collections.singletonList(
                        new Todo("todoId","lorem ipsum","COMPLETED",1)
                ),
                expectedEventsForTodo);
        assertThat(graph).isEqualTo(expectedGraph);
    }
}
