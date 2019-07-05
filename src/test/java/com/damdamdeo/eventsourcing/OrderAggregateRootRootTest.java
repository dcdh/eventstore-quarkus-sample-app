package com.damdamdeo.eventsourcing;

import com.damdamdeo.eventsourcing.domain.Event;
import com.damdamdeo.eventsourcing.domain.EventRepository;
import com.damdamdeo.order.domain.OrderAggregateRoot;
import com.damdamdeo.order.domain.OrderAggregateRootRepository;
import com.damdamdeo.order.domain.event.CreateOrderEventPayload;
import com.damdamdeo.order.domain.event.SendOrderEventPayload;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class OrderAggregateRootRootTest {

    @Inject
    OrderAggregateRootRepository orderAggregateRootRepository;

    @Inject
    EventRepository eventRepository;

    @Inject
    EntityManager em;

    @BeforeEach
    @Transactional
    public void setup() {
        em.createQuery("DELETE FROM EventEntity").executeUpdate();
    }

    @Test
    public void should_create_order() {
        // Given
        final OrderAggregateRoot orderAggregateRoot = new OrderAggregateRoot();
        orderAggregateRoot.apply(new CreateOrderEventPayload("orderId", "articleName", 10l),
                Collections.singletonMap("user", "Damien"));

        // When
        final OrderAggregateRoot orderAggregateRootSaved = orderAggregateRootRepository.save(orderAggregateRoot);

        // Then
        assertEquals("orderId", orderAggregateRootSaved.aggregateRootId());
        assertEquals("articleName", orderAggregateRootSaved.articleName());
        assertEquals(Boolean.FALSE, orderAggregateRootSaved.send());
        assertEquals(10l, orderAggregateRootSaved.quantity());
        assertEquals(0l, orderAggregateRootSaved.version());

        final List<Event> events = eventRepository.load("orderId", "OrderAggregateRoot");
        assertEquals(1, events.size());
        assertNotNull(events.get(0).eventId());
        assertEquals("orderId", events.get(0).aggregateRootId());
        assertEquals("OrderAggregateRoot", events.get(0).aggregateRootType());
        assertEquals("CreateOrderEvent", events.get(0).eventType());
        assertEquals(Boolean.FALSE, orderAggregateRootSaved.send());
        assertEquals(0L, events.get(0).version());
        assertNotNull(events.get(0).creationDate());
        assertEquals(Collections.singletonMap("user", "Damien"), events.get(0).metaData());
        assertEquals(new CreateOrderEventPayload("orderId", "articleName", 10L), events.get(0).payload());
    }

    @Test
    public void should_send_order() {
        // Given
        final OrderAggregateRoot orderAggregateRoot = new OrderAggregateRoot();
        orderAggregateRoot.apply(new CreateOrderEventPayload("orderId", "articleName", 10l),
                Collections.singletonMap("user", "Damien"));
        orderAggregateRoot.apply(new SendOrderEventPayload("orderId"));

        // When
        final OrderAggregateRoot orderAggregateRootSaved = orderAggregateRootRepository.save(orderAggregateRoot);

        // Then
        assertEquals(Boolean.TRUE, orderAggregateRootSaved.send());
        assertEquals(1l, orderAggregateRootSaved.version());

        final List<Event> events = eventRepository.load("orderId", "OrderAggregateRoot");
        assertEquals(2, events.size());
        assertEquals(new SendOrderEventPayload("orderId"), events.get(1).payload());
    }

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


}
