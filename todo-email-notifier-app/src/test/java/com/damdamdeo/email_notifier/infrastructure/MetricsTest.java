package com.damdamdeo.email_notifier.infrastructure;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class MetricsTest {

    @Test
    public void should_publish_metrics() {
        // Given

        // When && Then
        RestAssured
                .given()
                .accept("application/json")
                .when()
                .get("/metrics")
                .then()
                .body("base", notNullValue())
                .body("vendor", notNullValue())
                .body("application", notNullValue())
                .statusCode(200);
    }

}
