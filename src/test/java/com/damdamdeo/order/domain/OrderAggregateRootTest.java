package com.damdamdeo.order.domain;

import com.damdamdeo.eventsourcing.domain.Event;
import com.damdamdeo.eventsourcing.domain.EventRepository;
import com.damdamdeo.order.domain.event.CreateOrderEventPayload;
import com.damdamdeo.order.domain.event.SendOrderEventPayload;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class OrderAggregateRootTest extends AbstractOrderTest {

    @Inject
    OrderAggregateRootRepository orderAggregateRootRepository;

    @Inject
    EventRepository eventRepository;

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

}
