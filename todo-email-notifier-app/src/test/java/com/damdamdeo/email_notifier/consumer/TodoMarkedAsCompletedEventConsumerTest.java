package com.damdamdeo.email_notifier.consumer;

import com.damdamdeo.email_notifier.consumer.event.TodoAggregateTodoMarkedAsCompletedEventPayloadConsumer;
import com.damdamdeo.email_notifier.domain.EmailNotifier;
import com.damdamdeo.email_notifier.domain.TemplateGenerator;
import com.damdamdeo.email_notifier.domain.TodoMarkedAsCompleted;
import com.damdamdeo.email_notifier.infrastructure.TodoEntity;
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

// This is not working I need to use a custom repository instead
//    @InjectMock
//    EntityManager mockEntityManager;
    @InjectMock
    TodoRepository mockTodoRepository;

    @InjectMock
    TemplateGenerator mockTemplateGenerator;

    @InjectMock
    EmailNotifier mockEmailNotifier;

    @Test
    public void should_consume_todo_marked_as_completed_event() throws Exception {
        // Given
        final AggregateRootEventConsumable mockAggregateRootEventConsumable = mock(AggregateRootEventConsumable.class);
        final AggregateRootEventId mockAggregateRootEventId = mock(AggregateRootEventId.class);
        doReturn(mockAggregateRootEventId).when(mockAggregateRootEventConsumable).eventId();
        final TodoAggregateTodoMarkedAsCompletedEventPayloadConsumer mockTodoAggregateTodoMarkedAsCompletedEventPayloadConsumer = mock(TodoAggregateTodoMarkedAsCompletedEventPayloadConsumer.class);
        doReturn("todoId").when(mockTodoAggregateTodoMarkedAsCompletedEventPayloadConsumer).todoId();
        doReturn(mockTodoAggregateTodoMarkedAsCompletedEventPayloadConsumer).when(mockAggregateRootEventConsumable).eventPayload();
        final TodoEntity mockTodoEntity = mock(TodoEntity.class);
        doReturn("todoId").when(mockTodoEntity).todoId();
        doReturn("description").when(mockTodoEntity).description();
        doReturn(mockTodoEntity).when(mockTodoRepository).find("todoId");
        doReturn("content").when(mockTemplateGenerator).generate(new DefaultTodoMarkedAsCompleted("todoId", "description"));

        // When
        todoMarkedAsCompletedEventConsumer.consume(mockAggregateRootEventConsumable);

        // Then
        verify(mockTodoEntity, times(1)).markAsCompleted(mockAggregateRootEventId);
        verify(mockTodoRepository, times(1)).find(any());
        verify(mockTodoRepository, times(1)).merge(mockTodoEntity);
        verify(mockEmailNotifier, times(1)).notify("Todo marked as completed", "content");

        verify(mockTodoEntity, times(1)).todoId();
        verify(mockTodoEntity, times(1)).description();
        verify(mockAggregateRootEventConsumable, times(1)).eventId();
        verify(mockTodoAggregateTodoMarkedAsCompletedEventPayloadConsumer, times(1)).todoId();
        verify(mockAggregateRootEventConsumable, times(1)).eventPayload();
        verify(mockTemplateGenerator, times(1)).generate(any(TodoMarkedAsCompleted.class));
        verifyNoMoreInteractions(mockAggregateRootEventConsumable, mockAggregateRootEventId, mockTodoAggregateTodoMarkedAsCompletedEventPayloadConsumer, mockTodoEntity, mockTodoRepository, mockEmailNotifier, mockTemplateGenerator);
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
