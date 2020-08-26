package com.damdamdeo.todo.command.handler;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.UnknownAggregateRootException;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.command.CommandExecutor;
import com.damdamdeo.todo.aggregate.TodoAggregateRoot;
import com.damdamdeo.todo.aggregate.TodoAggregateRootRepository;
import com.damdamdeo.todo.command.MarkTodoAsCompletedCommand;
import com.damdamdeo.todo.domain.api.Todo;
import com.damdamdeo.todo.domain.api.UnknownTodoException;
import com.damdamdeo.todo.domain.api.shared.specification.Specification;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.junit.mockito.InjectSpy;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@QuarkusTest
public class MarkTodoAsCompletedCommandHandlerTest {

    @Inject
    MarkTodoAsCompletedCommandHandler markTodoAsCompletedCommandHandler;

    @InjectMock
    TodoAggregateRootRepository mockTodoAggregateRootRepository;

    @InjectSpy
    CommandExecutor spyCommandExecutor;

    @Test
    public void should_mark_todo_as_completed() throws Throwable {
        // Given
        final TodoAggregateRoot todoAggregateRoot = mock(TodoAggregateRoot.class);
        final Specification<Todo> canMarkTodoAsCompletedSpecification = mock(Specification.class);
        doReturn(canMarkTodoAsCompletedSpecification).when(todoAggregateRoot).canMarkTodoAsCompletedSpecification();
        doReturn(todoAggregateRoot).when(mockTodoAggregateRootRepository).load("todoId");
        final MarkTodoAsCompletedCommand markTodoAsCompletedCommand = new MarkTodoAsCompletedCommand("todoId");

        // When
        markTodoAsCompletedCommandHandler.execute(markTodoAsCompletedCommand);

        // Then
        verify(todoAggregateRoot, times(1)).handle(markTodoAsCompletedCommand);
        verify(mockTodoAggregateRootRepository, times(1)).load(any());
        verify(todoAggregateRoot, times(1)).handle(any(MarkTodoAsCompletedCommand.class));
        verify(todoAggregateRoot, times(1)).canMarkTodoAsCompletedSpecification();
        verify(canMarkTodoAsCompletedSpecification, times(1)).checkSatisfiedBy(todoAggregateRoot);
        verify(mockTodoAggregateRootRepository, times(1)).save(todoAggregateRoot);
        verifyNoMoreInteractions(mockTodoAggregateRootRepository, mockTodoAggregateRootRepository,
                canMarkTodoAsCompletedSpecification,
                todoAggregateRoot);
    }

    @Test
    public void should_throw_UnknownAggregateRootException_when_marking_as_completed_an_unknown_aggregate() throws Throwable {
        // Given
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);
        doThrow(new UnknownAggregateRootException(aggregateRootId)).when(mockTodoAggregateRootRepository).load("todoId");
        final MarkTodoAsCompletedCommand markTodoAsCompletedCommand = new MarkTodoAsCompletedCommand("todoId");

        // When && Then
        final UnknownTodoException unknownTodoException = assertThrows(UnknownTodoException.class, () -> markTodoAsCompletedCommandHandler.execute(markTodoAsCompletedCommand));
        assertEquals(new UnknownTodoException("todoId"), unknownTodoException);
        verify(mockTodoAggregateRootRepository, times(1)).load(any());
    }

    @Test
    public void should_use_executor_to_execute_command() throws Throwable {
        // Given
        final MarkTodoAsCompletedCommand markTodoAsCompletedCommand = new MarkTodoAsCompletedCommand("todoId");
        final TodoAggregateRoot todoAggregateRoot = mock(TodoAggregateRoot.class, RETURNS_DEEP_STUBS);
        doReturn(todoAggregateRoot).when(mockTodoAggregateRootRepository).load("todoId");

        // When
        markTodoAsCompletedCommandHandler.execute(markTodoAsCompletedCommand);

        // Then
        verify(spyCommandExecutor, times(1)).execute(any());
        verify(mockTodoAggregateRootRepository, times(1)).load(any());
    }

}
