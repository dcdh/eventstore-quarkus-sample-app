package com.damdamdeo.order.domain;

import io.quarkus.test.junit.QuarkusTest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import javax.ws.rs.core.MediaType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

@QuarkusTest
public class OrderEndpointTest extends AbstractOrderTest {

    @Test
    public void should_api_create_order() {
        given()
                .contentType("application/json")
                .body("{\"articleName\":\"articleName\", \"orderId\":\"orderId\", \"quantity\":10}")
                .when()
                .post("/orders/createNewOrder")
                .then()
                .statusCode(200)
                .body("orderId", equalTo("orderId"))
                .body("articleName", equalTo("articleName"))
                .body("quantity", equalTo(10))
                .body("version", equalTo(0))
        ;
    }

    @Test
    public void should_api_send_order() {
        given()
                .contentType("application/json")
                .body("{\"articleName\":\"articleName\", \"orderId\":\"orderId\", \"quantity\":10}")
                .when()
                .post("/orders/createNewOrder")
                .then()
                .statusCode(200);
        given()
                .contentType("application/json")
                .body("{\"orderId\":\"orderId\"}")
                .when()
                .post("/orders/sendOrder")
                .then()
                .statusCode(200)
                .body("send", equalTo(Boolean.TRUE))
                .body("version", equalTo(1));
    }

    @Test
    public void should_fail_when_orderId_already_affected() {
        given()
                .contentType("application/json")
                .body("{\"articleName\":\"articleName\", \"orderId\":\"orderId\", \"quantity\":10}")
                .when()
                .post("/orders/createNewOrder")
                .then()
                .statusCode(200);
        given()
                .contentType("application/json")
                .body("{\"articleName\":\"articleName2\", \"orderId\":\"orderId\", \"quantity\":10}")
                .when()
                .post("/orders/createNewOrder")
                .then()
                .statusCode(409)
                .contentType(MediaType.TEXT_PLAIN)
                .body(Matchers.equalTo("L'orderId 'orderId' est déjà affecté."));
    }

}
