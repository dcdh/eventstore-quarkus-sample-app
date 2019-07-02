package com.damdamdeo.eventsourcing;

import com.damdamdeo.eventsourcing.domain.Event;
import com.damdamdeo.eventsourcing.domain.EventRepository;
import com.wineforex.order.domain.OrderAggregate;
import com.wineforex.order.domain.OrderAggregateRootRepository;
import com.wineforex.order.domain.event.CreateOrderEventPayload;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class OrderAggregateRootTest {

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
        final OrderAggregate orderAggregate = new OrderAggregate();
        orderAggregate.apply(new CreateOrderEventPayload("orderId", "articleName", 10l),
                Collections.singletonMap("user", "Damien"));

        // When
        final OrderAggregate orderAggregateSaved = orderAggregateRootRepository.save(orderAggregate);

        // Then
        assertEquals("orderId", orderAggregateSaved.aggregateRootId());
        assertEquals("articleName", orderAggregateSaved.articleName());
        assertEquals(10l, orderAggregateSaved.quantity());
        assertEquals(0l, orderAggregateSaved.version());

        final List<Event> events = eventRepository.load("orderId", "OrderAggregate");
        assertEquals(1, events.size());
        assertNotNull(events.get(0).eventId());
        assertEquals("orderId", events.get(0).aggregateRootId());
        assertEquals("OrderAggregate", events.get(0).aggregateRootType());
        assertEquals("CreateOrderEvent", events.get(0).eventType());
        assertEquals(0L, events.get(0).version());
        assertNotNull(events.get(0).creationDate());
        assertEquals(Collections.singletonMap("user", "Damien"), events.get(0).metaData());
        assertEquals(new CreateOrderEventPayload("orderId", "articleName", 10L), events.get(0).payload());
    }

    // TODO deuxieme events !! OrderDeliveredEvent

//    @Test
//    public void should_api_create_order() {
//        given()
//                .when().get("/fruits")
//                .then()
//                .statusCode(200)
//                .body(
//                        containsString("Cherry"),
//                        containsString("Apple"),
//                        containsString("Banana")
//                );
//
//    }
//    TODO faire une api rest et tester aussi via swagger le tout en native !!!
//    le but etant de tester si le json est bien sauvegarder et restitué !!!


}
