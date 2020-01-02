package com.damdamdeo.todo_graph_visualiser;

import com.damdamdeo.todo_graph_visualiser.infrastructure.Neo4JGraphRepository;
import io.vertx.core.json.JsonObject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;

public class CommonTest {

    @ConfigProperty(name = "quarkus.neo4j.authentication.username")
    String username;

    @ConfigProperty(name = "quarkus.neo4j.authentication.password")
    String password;

    @Inject
    Neo4JGraphRepository neo4JGraphRepository;

    @BeforeEach
    public void setup() {
        given()
                .auth()
                .basic(username, password)
                .body("{\n" +
                        "  \"statements\" : [ {\n" +
                        "    \"statement\" : \"MATCH (n) DETACH DELETE n\"\n" +
                        "  } ]\n" +
                        "}")
                .accept("application/json; charset=UTF-8 ")
                .contentType("application/json")
                .when()
                .post("http://localhost:7474/db/data/transaction/commit ")
                .then()
                .statusCode(200);
    }

    protected void persistTodoCreatedEvent() {
        neo4JGraphRepository.persistTodoCreatedEvent("873ecba4-3f2e-4663-b9f1-b912e17bfc9b",
                "todoId",
                1562890044742000L,
                new JsonObject(),
                new JsonObject("{\"@aggregaterootType\": \"TodoAggregateRoot\", \"@payloadType\": \"TodoCreatedEventPayload\", \"todoId\": \"todoId\", \"description\": \"lorem ipsum\"}"),
                0L);
    }

    protected void persistTodoMarkedAsCompletedEvent() {
        neo4JGraphRepository.persistTodoMarkedAsCompletedEvent(
                "27f243d6-ba3a-468f-8435-4537e86ae64b",
                "todoId",
                1562890044922000L,
                new JsonObject(),
                new JsonObject("{\"@aggregaterootType\": \"TodoAggregateRoot\", \"@payloadType\": \"TodoMarkedAsCompletedEventPayload\", \"todoId\": \"todoId\"}"),
                1L);
    }

    protected void persistTodoAggregate() {
        neo4JGraphRepository.persistTodoAggregate("todoId",
                new JsonObject("{\"version\": 1, \"todoStatus\": \"COMPLETED\", \"description\": \"lorem ipsum\", \"aggregateRootId\": \"todoId\", \"@aggregaterootType\": \"TodoAggregateRoot\"}"),
                1L);
    }

}
