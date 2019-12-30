package com.damdamdeo.todo_graph_visualiser;

import com.damdamdeo.todo_graph_visualiser.infrastructure.Neo4JRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.JsonObject;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class Neo4JRepositoryTest extends CommonTest {

    @Inject
    Neo4JRepository neo4JRepository;

    @Test
    public void should_create_expected_relationship_on_TodoCreatedEvent() {
        // Given
        // When
        neo4JRepository.persistTodoCreatedEvent("873ecba4-3f2e-4663-b9f1-b912e17bfc9b",
                "todoId",
                1562890044742000L,
                new JsonObject(),
                new JsonObject("{\"@aggregaterootType\": \"TodoAggregateRoot\", \"@payloadType\": \"TodoCreatedEventPayload\", \"todoId\": \"todoId\", \"description\": \"lorem ipsum\"}"),
                0L);

        // Then
        given()
                .auth()
                .basic(username, password)
                .body("{\n" +
                        "  \"statements\" : [ {\n" +
                        "    \"statement\" : \"MATCH (n)-[r { eventId: '873ecba4-3f2e-4663-b9f1-b912e17bfc9b' } ]-(m) RETURN r\"\n" +
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
                .body("results[0].data[0].row[0].eventId", CoreMatchers.equalTo("873ecba4-3f2e-4663-b9f1-b912e17bfc9b"))
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
        neo4JRepository.persistTodoCreatedEvent("873ecba4-3f2e-4663-b9f1-b912e17bfc9b",
                "todoId",
                1562890044742000L,
                new JsonObject(),
                new JsonObject("{\"@aggregaterootType\": \"TodoAggregateRoot\", \"@payloadType\": \"TodoCreatedEventPayload\", \"todoId\": \"todoId\", \"description\": \"lorem ipsum\"}"),
                0L);

        // When
        neo4JRepository.persistTodoMarkedAsCompletedEvent(
                "27f243d6-ba3a-468f-8435-4537e86ae64b",
                "todoId",
                1562890044922000L,
                new JsonObject(),
                new JsonObject("{\"@aggregaterootType\": \"TodoAggregateRoot\", \"@payloadType\": \"TodoMarkedAsCompletedEventPayload\", \"todoId\": \"todoId\"}"),
                1L);

        // Then
        given()
                .auth()
                .basic(username, password)
                .body("{\n" +
                        "  \"statements\" : [ {\n" +
                        "    \"statement\" : \"MATCH (n)-[r { eventId: '27f243d6-ba3a-468f-8435-4537e86ae64b' } ]-(m) RETURN r\"\n" +
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
                .body("results[0].data[0].row[0].eventId", CoreMatchers.equalTo("27f243d6-ba3a-468f-8435-4537e86ae64b"))
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
        neo4JRepository.persistTodoCreatedEvent("873ecba4-3f2e-4663-b9f1-b912e17bfc9b",
                "todoId",
                1562890044742000L,
                new JsonObject(),
                new JsonObject("{\"@aggregaterootType\": \"TodoAggregateRoot\", \"@payloadType\": \"TodoCreatedEventPayload\", \"todoId\": \"todoId\", \"description\": \"lorem ipsum\"}"),
                0L);

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
        neo4JRepository.persistTodoAggregate("todoId",
                new JsonObject("{\"version\": 1, \"todoStatus\": \"COMPLETED\", \"description\": \"lorem ipsum\", \"aggregateRootId\": \"todoId\", \"@aggregaterootType\": \"TodoAggregateRoot\"}"),
                1L);

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
        neo4JRepository.persistTodoCreatedEvent("873ecba4-3f2e-4663-b9f1-b912e17bfc9b",
                "todoId",
                1562890044742000L,
                new JsonObject(),
                new JsonObject("{\"@aggregaterootType\": \"TodoAggregateRoot\", \"@payloadType\": \"TodoCreatedEventPayload\", \"todoId\": \"todoId\", \"description\": \"lorem ipsum\"}"),
                0L);
        neo4JRepository.persistTodoAggregate("todoId",
                new JsonObject("{\"version\": 1, \"todoStatus\": \"COMPLETED\", \"description\": \"lorem ipsum\", \"aggregateRootId\": \"todoId\", \"@aggregaterootType\": \"TodoAggregateRoot\"}"),
                1L);

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
                .body("results[0].data[0].row[1].eventId", CoreMatchers.equalTo("873ecba4-3f2e-4663-b9f1-b912e17bfc9b"))
                .body("results[0].data[0].row[1].description", CoreMatchers.equalTo("lorem ipsum"))
                .body("results[0].data[0].row[1].eventType", CoreMatchers.equalTo("TodoCreatedEvent"))
                .body("results[0].data[0].row[1].todoId", CoreMatchers.equalTo("todoId"))
                .body("results[0].data[0].row[1].creationDate", CoreMatchers.equalTo(1562890044742000L))
                .body("results[0].data[0].row[1].version", CoreMatchers.equalTo(0))
                .statusCode(200);
    }

}
