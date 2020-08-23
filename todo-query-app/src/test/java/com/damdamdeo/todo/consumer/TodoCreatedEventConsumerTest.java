package com.damdamdeo.todo.consumer;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumable;
import com.damdamdeo.todo.domain.api.TodoStatus;
import com.damdamdeo.todo.infrastructure.JpaTodoRepository;
import com.damdamdeo.todo.infrastructure.TodoEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@QuarkusTest
public class TodoCreatedEventConsumerTest {

    @Inject
    TodoCreatedEventConsumer todoCreatedEventConsumer;

// This is not working I need to use a custom repository instead
//    @InjectMock
//    EntityManager mockEntityManager;
    @InjectMock
    JpaTodoRepository mockJpaTodoRepository;

    @Test
    public void should_consume_todo_created_event() throws Exception {
        // Given
        final ObjectMapper objectMapper = new ObjectMapper();
        final AggregateRootEventConsumable mockAggregateRootEventConsumable = mock(AggregateRootEventConsumable.class, RETURNS_DEEP_STUBS);
        when(mockAggregateRootEventConsumable.eventId().version()).thenReturn(0l);
        final JsonNode eventPayload = objectMapper.readTree("{\"todoId\":\"todoId\",\"description\":\"lorem ipsum\"}");
        doReturn(eventPayload).when(mockAggregateRootEventConsumable).eventPayload();

        // When
        todoCreatedEventConsumer.consume(mockAggregateRootEventConsumable);

        // Then
        final ArgumentCaptor<TodoEntity> todoEntityCaptor = ArgumentCaptor.forClass(TodoEntity.class);
        verify(mockJpaTodoRepository, times(1)).persist(todoEntityCaptor.capture());
        assertEquals("todoId", todoEntityCaptor.getValue().todoId());
        assertEquals("lorem ipsum", todoEntityCaptor.getValue().description());
        assertEquals(TodoStatus.IN_PROGRESS, todoEntityCaptor.getValue().todoStatus());
        assertEquals(0l, todoEntityCaptor.getValue().version());

        verify(mockAggregateRootEventConsumable.eventId(), times(1)).version();
        verify(mockAggregateRootEventConsumable, atLeastOnce()).eventPayload();
    }

    @Test
    public void should_apply_on_todo_aggregate_root() {
        assertEquals("TodoAggregateRoot", todoCreatedEventConsumer.aggregateRootType());
    }

    @Test
    public void should_apply_on_todo_created_event() {
        assertEquals("TodoCreatedEvent", todoCreatedEventConsumer.eventType());
    }

}
