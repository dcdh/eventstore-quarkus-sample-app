package com.damdamdeo.email_notifier.consumer;

import com.damdamdeo.email_notifier.domain.EmailNotifier;
import com.damdamdeo.email_notifier.domain.TemplateGenerator;
import com.damdamdeo.email_notifier.domain.Todo;
import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumable;
import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@QuarkusTest
public class TodoMarkedAsCompletedEventConsumerTest {

    @Inject
    TodoMarkedAsCompletedEventConsumer todoMarkedAsCompletedEventConsumer;

    @InjectMock
    TemplateGenerator mockTemplateGenerator;

    @InjectMock
    EmailNotifier mockEmailNotifier;

    @Test
    public void should_consume_todo_marked_as_completed_event() throws Exception {
        // Given
        final Todo mockTodo = mock(Todo.class);
        final AggregateRootEventConsumable mockAggregateRootEventConsumable = mock(AggregateRootEventConsumable.class);
        final AggregateRootEventId mockAggregateRootEventId = mock(AggregateRootEventId.class);
        doReturn(mockAggregateRootEventId).when(mockAggregateRootEventConsumable).eventId();
        final TodoAggregateRootMaterializedStateConsumer mockTodoAggregateRootMaterializedStateConsumer = mock(TodoAggregateRootMaterializedStateConsumer.class);
        doReturn(mockTodo).when(mockTodoAggregateRootMaterializedStateConsumer).toDomain();
        doReturn(mockTodoAggregateRootMaterializedStateConsumer).when(mockAggregateRootEventConsumable).materializedState();
        doReturn("content").when(mockTemplateGenerator).generateTodoMarkedAsCompleted(mockTodo);

        // When
        todoMarkedAsCompletedEventConsumer.consume(mockAggregateRootEventConsumable);

        // Then
        verify(mockEmailNotifier, times(1)).notify("Todo marked as completed", "content");

        verify(mockTodoAggregateRootMaterializedStateConsumer, times(1)).toDomain();
        verify(mockAggregateRootEventConsumable, times(1)).materializedState();
        verify(mockTemplateGenerator, times(1)).generateTodoMarkedAsCompleted(any());
        verifyNoMoreInteractions(mockAggregateRootEventConsumable, mockAggregateRootEventId, mockTodoAggregateRootMaterializedStateConsumer, mockTodo, mockEmailNotifier, mockTemplateGenerator);
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
