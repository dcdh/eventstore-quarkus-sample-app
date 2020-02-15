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
        neo4JGraphRepository.persistTodoCreatedEvent("todoId",
                1562890044742000L,
                new JsonObject(),
                new JsonObject("{\"@type\":\"TodoAggregateTodoCreatedEventPayload\",\"todoId\":\"todoId\",\"description\":\"lorem ipsum\"}"),
                0L);
    }

    protected void persistTodoMarkedAsCompletedEvent() {
        neo4JGraphRepository.persistTodoMarkedAsCompletedEvent(
                "todoId",
                1562890044922000L,
                new JsonObject(),
                new JsonObject("{\"@type\": \"TodoAggregateTodoMarkedAsCompletedEventPayload\", \"todoId\": \"todoId\"}"),
                1L);
    }

    protected void persistTodoAggregate() {
        neo4JGraphRepository.persistTodoAggregate("todoId",
                new JsonObject("{\"version\": 1, \"todoStatus\": \"COMPLETED\", \"description\": \"lorem ipsum\", \"aggregateRootId\": \"todoId\", \"@type\": \"TodoAggregateRoot\", \"aggregateRootType\": \"TodoAggregateRoot\"}"),
                1L);
    }

}
