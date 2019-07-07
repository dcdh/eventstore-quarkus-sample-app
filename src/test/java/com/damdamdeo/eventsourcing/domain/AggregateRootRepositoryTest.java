package com.damdamdeo.eventsourcing.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AggregateRootRepositoryTest {

    private AbstractAggregateRootRepository abstractAggregateRootRepository;
    private EventRepository eventRepository;
    private AggregateRootProjectionRepository aggregateRootProjectionRepository;

    @BeforeEach
    public void setup() {
        aggregateRootProjectionRepository = mock(AggregateRootProjectionRepository.class);
        abstractAggregateRootRepository = spy(AbstractAggregateRootRepository.class);
        eventRepository = mock(EventRepository.class);
        doReturn(eventRepository).when(abstractAggregateRootRepository).eventRepository();
        doReturn(aggregateRootProjectionRepository).when(abstractAggregateRootRepository).aggregateRootProjectionRepository();
    }

    @Test
    public void should_save_events() {
        // Given
        final AggregateRoot aggregateRoot = mock(AggregateRoot.class);
        final List<Event> unsavedEvents = Collections.singletonList(mock(Event.class));
        doReturn(unsavedEvents).when(aggregateRoot).unsavedEvents();

        // When
        final AggregateRoot aggregateRootSaved = abstractAggregateRootRepository.save(aggregateRoot);

        // Then
        assertEquals(aggregateRoot, aggregateRootSaved);
        verify(eventRepository).save(unsavedEvents);
        verify(aggregateRoot).unsavedEvents();
    }

    @Test
    public void should_purge_events_after_save() {
        // Given
        final AggregateRoot aggregateRoot = mock(AggregateRoot.class);

        // When
        abstractAggregateRootRepository.save(aggregateRoot);

        // Then
        verify(aggregateRoot).deleteUnsavedEvents();
    }

    @Test
    public void should_save_aggregate_projection() {
        // Given
        final AggregateRoot aggregateRoot = mock(AggregateRoot.class);

        // When
        abstractAggregateRootRepository.save(aggregateRoot);

        // Then
        verify(aggregateRootProjectionRepository).save(any(AggregateRootProjection.class));
    }

    @Test
    public void should_load_aggregateRoot() {
        // Given
        final AggregateRoot aggregateRoot = mock(AggregateRoot.class);
        doReturn(aggregateRoot).when(abstractAggregateRootRepository).createNewInstance();
        final List<Event> events = Collections.singletonList(mock(Event.class));
        doReturn(events).when(eventRepository).load(eq("aggregateRootId"), anyString());

        // When
        final AggregateRoot aggregateRootLoaded = abstractAggregateRootRepository.load("aggregateRootId");

        // Then
        assertEquals(aggregateRoot, aggregateRootLoaded);
        verify(aggregateRoot).loadFromHistory(events);
        verify(abstractAggregateRootRepository).createNewInstance();
        verify(eventRepository).load(eq("aggregateRootId"), anyString());
    }

    @Test
    public void should_throw_exception_when_no_events_are_presents() {
        // Given
        final AggregateRoot aggregateRoot = mock(AggregateRoot.class);
        doReturn(aggregateRoot).when(abstractAggregateRootRepository).createNewInstance();
        doReturn(Collections.emptyList()).when(eventRepository).load(eq("aggregateRootId"), anyString());

        // When && Then
        Assertions.assertThrows(UnknownAggregateRootException.class, () -> {
            abstractAggregateRootRepository.load("aggregateRootId");
        });

        verify(aggregateRoot, never()).loadFromHistory(anyList());
        verify(abstractAggregateRootRepository).createNewInstance();
        verify(eventRepository).load(eq("aggregateRootId"), anyString());
    }

}
