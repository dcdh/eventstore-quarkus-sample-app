package com.damdamdeo.todo.domain;

import com.damdamdeo.eventsourcing.domain.Event;
import com.damdamdeo.eventsourcing.domain.EventRepository;
import com.damdamdeo.todo.api.TodoAggregateRootRepository;
import com.damdamdeo.todo.api.TodoStatus;
import com.damdamdeo.todo.domain.event.TodoCreatedEventPayload;
import com.damdamdeo.todo.domain.event.TodoMarkedAsCompletedEventPayload;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class TodoAggregateRootTest extends AbstractTodoTest {

    @Inject
    TodoAggregateRootRepository todoAggregateRootRepository;

    @Inject
    EventRepository eventRepository;

    @Test
    public void should_create_todo() {
        // Given
        final TodoAggregateRoot todoAggregateRoot = new TodoAggregateRoot();
        todoAggregateRoot.apply(new TodoCreatedEventPayload("todoId", "lorem ipsum"),
                Collections.singletonMap("user", "Damien"));

        // When
        final TodoAggregateRoot todoAggregateRootSaved = todoAggregateRootRepository.save(todoAggregateRoot);

        // Then
        assertEquals("todoId", todoAggregateRootSaved.aggregateRootId());
        assertEquals("lorem ipsum", todoAggregateRootSaved.description());
        assertEquals(TodoStatus.IN_PROGRESS, todoAggregateRootSaved.todoStatus());
        assertEquals(0l, todoAggregateRootSaved.version());

        final List<Event> events = eventRepository.load("todoId", "TodoAggregateRoot");
        assertEquals(1, events.size());
        assertNotNull(events.get(0).eventId());
        assertEquals("todoId", events.get(0).aggregateRootId());
        assertEquals("TodoAggregateRoot", events.get(0).aggregateRootType());
        assertEquals("TodoCreatedEvent", events.get(0).eventType());
        assertEquals(0L, events.get(0).version());
        assertNotNull(events.get(0).creationDate());
        assertEquals(Collections.singletonMap("user", "Damien"), events.get(0).metaData());
        assertEquals(new TodoCreatedEventPayload("todoId", "lorem ipsum"), events.get(0).payload());
    }

    @Test
    public void should_mark_todo_as_completed() {
        // Given
        final TodoAggregateRoot todoAggregateRoot = new TodoAggregateRoot();
        todoAggregateRoot.apply(new TodoCreatedEventPayload("todoId", "lorem ipsum"),
                Collections.singletonMap("user", "Damien"));
        todoAggregateRoot.apply(new TodoMarkedAsCompletedEventPayload("todoId"));

        // When
        final TodoAggregateRoot todoAggregateRootSaved = todoAggregateRootRepository.save(todoAggregateRoot);

        // Then
        assertEquals(TodoStatus.COMPLETED, todoAggregateRootSaved.todoStatus());
        assertEquals(1l, todoAggregateRootSaved.version());

        final List<Event> events = eventRepository.load("todoId", "TodoAggregateRoot");
        assertEquals(2, events.size());
        assertEquals(new TodoMarkedAsCompletedEventPayload("todoId"), events.get(1).payload());
    }

}
