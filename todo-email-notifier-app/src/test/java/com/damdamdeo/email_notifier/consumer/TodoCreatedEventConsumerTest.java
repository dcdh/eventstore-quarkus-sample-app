package com.damdamdeo.email_notifier.consumer;

import com.damdamdeo.email_notifier.consumer.event.TodoAggregateTodoCreatedEventPayloadConsumer;
import com.damdamdeo.email_notifier.domain.*;
import com.damdamdeo.email_notifier.infrastructure.TodoEntity;
import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootEventConsumable;
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
    TodoRepository mockTodoRepository;

    @InjectMock
    TemplateGenerator mockTemplateGenerator;

    @InjectMock
    EmailNotifier mockEmailNotifier;

    @Test
    public void should_consume_todo_created_event() throws Exception {
        // Given
        final AggregateRootEventConsumable mockAggregateRootEventConsumable = mock(AggregateRootEventConsumable.class, RETURNS_DEEP_STUBS);
        when(mockAggregateRootEventConsumable.eventId().version()).thenReturn(0l);
        final TodoAggregateTodoCreatedEventPayloadConsumer mockTodoAggregateTodoCreatedEventPayloadConsumer = mock(TodoAggregateTodoCreatedEventPayloadConsumer.class);
        doReturn("todoId").when(mockTodoAggregateTodoCreatedEventPayloadConsumer).todoId();
        doReturn("description").when(mockTodoAggregateTodoCreatedEventPayloadConsumer).description();
        doReturn(mockTodoAggregateTodoCreatedEventPayloadConsumer).when(mockAggregateRootEventConsumable).eventPayload();
        doReturn("content").when(mockTemplateGenerator).generate(new DefaultTodoCreated("todoId", "description"));

        // When
        todoCreatedEventConsumer.consume(mockAggregateRootEventConsumable);

        // Then
        final ArgumentCaptor<TodoEntity> todoEntityCaptor = ArgumentCaptor.forClass(TodoEntity.class);
        verify(mockTodoRepository, times(1)).persist(todoEntityCaptor.capture());
        assertEquals("todoId", todoEntityCaptor.getValue().todoId());
        assertEquals("description", todoEntityCaptor.getValue().description());
        assertEquals(TodoStatus.IN_PROGRESS, todoEntityCaptor.getValue().todoStatus());
        assertEquals(0l, todoEntityCaptor.getValue().version());
        verify(mockEmailNotifier, times(1)).notify("New Todo created", "content");

        verify(mockAggregateRootEventConsumable.eventId(), times(1)).version();
        verify(mockAggregateRootEventConsumable, atLeastOnce()).eventId();
        verify(mockTodoAggregateTodoCreatedEventPayloadConsumer, times(1)).todoId();
        verify(mockTodoAggregateTodoCreatedEventPayloadConsumer, times(1)).description();
        verify(mockAggregateRootEventConsumable, times(1)).eventPayload();
        verify(mockTemplateGenerator, times(1)).generate(any(DefaultTodoCreated.class));
        verifyNoMoreInteractions(mockAggregateRootEventConsumable, mockTodoAggregateTodoCreatedEventPayloadConsumer, mockTodoRepository, mockEmailNotifier, mockTemplateGenerator);
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
