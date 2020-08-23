package com.damdamdeo.email_notifier.consumer;

import com.damdamdeo.email_notifier.domain.*;
import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumable;
import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@QuarkusTest
public class TodoCreatedEventConsumerTest {

    @Inject
    TodoCreatedEventConsumer todoCreatedEventConsumer;

    @InjectMock
    TemplateGenerator mockTemplateGenerator;

    @InjectMock
    EmailNotifier mockEmailNotifier;

    @Test
    public void should_consume_todo_created_event_send_a_notification() throws Exception {
        // Given
        final ObjectMapper objectMapper = new ObjectMapper();
        final AggregateRootEventConsumable mockAggregateRootEventConsumable = mock(AggregateRootEventConsumable.class);
        final JsonNode materializedState = objectMapper.readTree("{\"todoId\":\"todoId\",\"description\":\"description\",\"todoStatus\":\"IN_PROGRESS\"}");
        final AggregateRootEventId aggregateRootEventId = mock(AggregateRootEventId.class);
        doReturn(0l).when(aggregateRootEventId).version();
        doReturn(materializedState).when(mockAggregateRootEventConsumable).materializedState();
        doReturn(aggregateRootEventId).when(mockAggregateRootEventConsumable).eventId();
        final Todo todo = new JsonNodeTodo(materializedState, aggregateRootEventId);
        doReturn("content").when(mockTemplateGenerator).generateTodoCreated(todo);

        // When
        todoCreatedEventConsumer.consume(mockAggregateRootEventConsumable);

        // Then
        verify(mockEmailNotifier, times(1)).notify("New Todo created", "content");
        verify(mockTemplateGenerator, times(1)).generateTodoCreated(todo);

        verify(mockAggregateRootEventConsumable, times(1)).materializedState();
        verify(mockAggregateRootEventConsumable, atLeastOnce()).eventId();
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
