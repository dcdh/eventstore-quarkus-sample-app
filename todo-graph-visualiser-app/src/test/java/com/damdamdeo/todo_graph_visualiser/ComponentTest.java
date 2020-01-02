package com.damdamdeo.todo_graph_visualiser;

import com.jayway.restassured.module.jsv.JsonSchemaValidator;
import io.quarkus.test.junit.QuarkusTest;
import org.apache.commons.io.IOUtils;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import javax.inject.Inject;

import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;

@QuarkusTest
public class ComponentTest extends CommonTest {

    @Inject
    KafkaDebeziumProducer kafkaDebeziumProducer;

    @Test
    public void should_consume_kafka_messages() throws Exception {
        // Given
        kafkaDebeziumProducer.produce("TodoCreatedEvent.json");
        kafkaDebeziumProducer.produce("TodoMarkedAsCompletedEvent.json");
        kafkaDebeziumProducer.produce("TodoAggregateProjection.json");

        // When && Then
        await().atMost(60, TimeUnit.SECONDS).until(() -> {
            // Attention I got a temp node when event is handled without a node present for aggregate.
            // I had to check that the version is present :) to ensure aggregate has well been created
            final Integer countNodes = given()
                    .auth()
                    .basic(username, password)
                    .body("{\n" +
                            "  \"statements\" : [ {\n" +
                            "    \"statement\" : \"MATCH (a { aggregateId: 'todoId', version: 1 }) RETURN COUNT(a)\"\n" +
                            "  } ]\n" +
                            "}")
                    .accept("application/json; charset=UTF-8 ")
                    .contentType("application/json")
                    .when()
                    .post("http://localhost:7474/db/data/transaction/commit")
                    .then()
                    .log()
                    .all()
                    .extract()
                    .body()
                    .jsonPath()
                    .getInt("results[0].data[0].row[0]");
            final Integer countRelationships = given()
                    .auth()
                    .basic(username, password)
                    .body("{\n" +
                            "  \"statements\" : [ {\n" +
                            "    \"statement\" : \"MATCH (a)-[r]->(b) RETURN COUNT(r)\"\n" +
                            "  } ]\n" +
                            "}")
                    .accept("application/json; charset=UTF-8 ")
                    .contentType("application/json")
                    .when()
                    .post("http://localhost:7474/db/data/transaction/commit")
                    .then()
                    .log()
                    .all()
                    .extract()
                    .body()
                    .jsonPath()
                    .getInt("results[0].data[0].row[0]");
            return countNodes.equals(1)
                    && countRelationships.equals(2);
        });

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

                .body("results[0].data[0].row[0].aggregateId", CoreMatchers.equalTo("todoId"))
                .body("results[0].data[0].row[0].version", CoreMatchers.equalTo(1))
                .body("results[0].data[0].row[1].eventId", CoreMatchers.equalTo("27f243d6-ba3a-468f-8435-4537e86ae64b"))

                .body("results[0].data[1].row[0].aggregateId", CoreMatchers.equalTo("todoId"))
                .body("results[0].data[1].row[0].version", CoreMatchers.equalTo(1))
                .body("results[0].data[1].row[1].eventId", CoreMatchers.equalTo("873ecba4-3f2e-4663-b9f1-b912e17bfc9b"))

                .statusCode(200);

        final String response = given()
                .accept("application/json; charset=UTF-8")
                .when()
                .get("http://localhost:8081/graph")// https://quarkus.io/guides/getting-started-testing explains about port on 8081
                .then()
                .log()
                .all()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("expected/graphSchema.json"))
                .statusCode(200)
                .extract().jsonPath().prettyPrint();
        JSONAssert.assertEquals(IOUtils.toString(this.getClass().getResourceAsStream("/expected/graphContent.json"),"UTF-8"),
                response, JSONCompareMode.NON_EXTENSIBLE);
    }

}
