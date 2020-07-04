package com.damdamdeo.email_notifier.consumer;

import com.damdamdeo.email_notifier.domain.*;
import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumable;
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
    public void should_consume_todo_created_event() throws Exception {
        // Given
        final Todo mockTodo = mock(Todo.class);
        final AggregateRootEventConsumable mockAggregateRootEventConsumable = mock(AggregateRootEventConsumable.class, RETURNS_DEEP_STUBS);
        final TodoAggregateRootMaterializedStateConsumer mockTodoAggregateRootMaterializedStateConsumer = mock(TodoAggregateRootMaterializedStateConsumer.class);
        doReturn(mockTodo).when(mockTodoAggregateRootMaterializedStateConsumer).toDomain();
        doReturn(mockTodoAggregateRootMaterializedStateConsumer).when(mockAggregateRootEventConsumable).materializedState();
        doReturn("content").when(mockTemplateGenerator).generateTodoCreated(mockTodo);

        // When
        todoCreatedEventConsumer.consume(mockAggregateRootEventConsumable);

        // Then
        verify(mockEmailNotifier, times(1)).notify("New Todo created", "content");

        verify(mockTodoAggregateRootMaterializedStateConsumer, times(1)).toDomain();
        verify(mockAggregateRootEventConsumable, times(1)).materializedState();
        verify(mockAggregateRootEventConsumable, times(1)).eventId();
        verify(mockTemplateGenerator, times(1)).generateTodoCreated(any());
        verifyNoMoreInteractions(mockAggregateRootEventConsumable, mockTodoAggregateRootMaterializedStateConsumer, mockTodo, mockEmailNotifier, mockTemplateGenerator);
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
