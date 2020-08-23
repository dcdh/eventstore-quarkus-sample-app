package com.damdamdeo.todo.consumer;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumable;
import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;
import com.damdamdeo.todo.infrastructure.JpaTodoRepository;
import com.damdamdeo.todo.infrastructure.TodoEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doReturn;

@QuarkusTest
public class TodoMarkedAsCompletedEventConsumerTest {

    @Inject
    TodoMarkedAsCompletedEventConsumer todoMarkedAsCompletedEventConsumer;

// This is not working I need to use a custom repository instead
//    @InjectMock
//    EntityManager mockEntityManager;
    @InjectMock
    JpaTodoRepository mockJpaTodoRepository;

    @Test
    public void should_consume_todo_marked_as_completed_event() throws Exception {
        // Given
        final ObjectMapper objectMapper = new ObjectMapper();
        final AggregateRootEventConsumable mockAggregateRootEventConsumable = mock(AggregateRootEventConsumable.class, RETURNS_DEEP_STUBS);
        final AggregateRootEventId mockAggregateRootEventId = mock(AggregateRootEventId.class);
        doReturn(mockAggregateRootEventId).when(mockAggregateRootEventConsumable).eventId();
        final JsonNode eventPayload = objectMapper.readTree("{\"todoId\":\"todoId\"}");
        doReturn(eventPayload).when(mockAggregateRootEventConsumable).eventPayload();
        final TodoEntity mockTodoEntity = mock(TodoEntity.class);
        doReturn(mockTodoEntity).when(mockJpaTodoRepository).find("todoId");

        // When
        todoMarkedAsCompletedEventConsumer.consume(mockAggregateRootEventConsumable);

        // Then
        verify(mockTodoEntity, times(1)).markAsCompleted(mockAggregateRootEventId);
        verify(mockJpaTodoRepository, times(1)).find("todoId");
        verify(mockJpaTodoRepository, times(1)).merge(mockTodoEntity);
        verify(mockAggregateRootEventConsumable, atLeastOnce()).eventId();
        verify(mockAggregateRootEventConsumable, times(1)).eventPayload();
    }

    @Test
    public void should_apply_on_todo_aggregate_root() {
        assertEquals("TodoAggregateRoot", todoMarkedAsCompletedEventConsumer.aggregateRootType());
    }

    @Test
    public void should_apply_on_todo_marked_as_completed_event() {
        assertEquals("TodoMarkedAsCompletedEvent", todoMarkedAsCompletedEventConsumer.eventType());
    }

}
