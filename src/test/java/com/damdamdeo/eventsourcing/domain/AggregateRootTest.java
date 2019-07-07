package com.damdamdeo.eventsourcing.domain;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class AggregateRootTest {

    private static class TestAggregateRoot extends AggregateRoot {

        public TestAggregateRoot(final String aggregateRootId) {
            this.aggregateRootId = aggregateRootId;
        }

    }

    // I know I can do better !!!
    @Test
    public void should_applying_add_new_event() {
        // Given
        final AggregateRoot aggregateRoot = spy(new TestAggregateRoot("0123456789"));
        final Payload payload = mock(Payload.class);
        final Map<String, Object> metaData = new HashMap<>();

        // When
        aggregateRoot.apply(payload);

        // Then
        assertEquals("0123456789", aggregateRoot.unsavedEvents().get(0).aggregateRootId());
        assertNotNull(aggregateRoot.unsavedEvents().get(0).aggregateRootType());
        assertNotNull(aggregateRoot.unsavedEvents().get(0).eventType());
        assertEquals(0, aggregateRoot.unsavedEvents().get(0).version());
        assertNotNull(aggregateRoot.unsavedEvents().get(0).creationDate());
        assertEquals(metaData, aggregateRoot.unsavedEvents().get(0).metaData());
        assertEquals(payload, aggregateRoot.unsavedEvents().get(0).payload());
        assertEquals(0l, aggregateRoot.version());
        assertEquals(1, aggregateRoot.unsavedEvents().size());
        assertNotNull(aggregateRoot.unsavedEvents().get(0).eventId());
    }

    @Test
    public void should_deleteUnsavedEvents_delete_unsaved_events() {
        // Given
        final AggregateRoot aggregateRoot = spy(new TestAggregateRoot("0123456789"));
        final Payload payload = mock(Payload.class);
        aggregateRoot.apply(payload);

        // When
        aggregateRoot.deleteUnsavedEvents();

        // Then
        assertEquals(0l, aggregateRoot.unsavedEvents().size());
    }

    @Test
    public void should_load_from_history_apply_given_events() {
        // Given
        final AggregateRoot aggregateRoot = spy(new TestAggregateRoot("0123456789"));
        final Event event = mock(Event.class);
        final Payload payload = mock(Payload.class);
        doReturn(0l).when(event).version();
        doReturn(payload).when(event).payload();
        final List<Event> events = Collections.singletonList(event);

        // When
        aggregateRoot.loadFromHistory(events);

        // Then
        verify(event).version();
        verify(event).payload();
        verify(payload).apply(aggregateRoot);
        assertEquals(0l, aggregateRoot.version());
        assertEquals(0l, aggregateRoot.unsavedEvents().size());
    }

}
