package com.damdamdeo.email_notifier.infrastructure;

import com.damdamdeo.email_notifier.domain.TodoStatus;
import com.damdamdeo.eventsourced.model.api.AggregateRootEventId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TodoEntityTest {

    @Test
    public void should_mark_todo_as_completed() {
        // Given
        final AggregateRootEventId eventId = mock(AggregateRootEventId.class);
        doReturn(1l).when(eventId).version();
        final TodoEntity todoEntity = new TodoEntity();

        // When
        todoEntity.markAsCompleted(eventId);

        // Then
        assertEquals(TodoStatus.COMPLETED, todoEntity.todoStatus());
        assertEquals(1l, todoEntity.version());
        verify(eventId, times(1)).version();
        verifyNoMoreInteractions(eventId);
    }
}
