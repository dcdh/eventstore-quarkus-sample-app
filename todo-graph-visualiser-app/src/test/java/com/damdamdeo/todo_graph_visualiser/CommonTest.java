package com.damdamdeo.todo_graph_visualiser;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;

import static io.restassured.RestAssured.given;

public class CommonTest {

    @ConfigProperty(name = "quarkus.neo4j.authentication.username")
    String username;

    @ConfigProperty(name = "quarkus.neo4j.authentication.password")
    String password;

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

}
