package com.damdamdeo.todo.domain.command.handler;

import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.UnknownAggregateRootException;
import com.damdamdeo.todo.domain.TodoAggregateRoot;
import com.damdamdeo.todo.domain.TodoAggregateRootRepository;
import com.damdamdeo.todo.domain.api.Todo;
import com.damdamdeo.todo.domain.api.UnknownTodoException;
import com.damdamdeo.todo.domain.api.shared.specification.Specification;
import com.damdamdeo.todo.domain.command.MarkTodoAsCompletedCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DomainMarkTodoAsCompletedCommandHandlerTest {

    DomainMarkTodoAsCompletedCommandHandler domainMarkTodoAsCompletedCommandHandler;

    TodoAggregateRootRepository mockTodoAggregateRootRepository;

    @BeforeEach
    public void setup() {
        mockTodoAggregateRootRepository = mock(TodoAggregateRootRepository.class);
        domainMarkTodoAsCompletedCommandHandler = new DomainMarkTodoAsCompletedCommandHandler(mockTodoAggregateRootRepository);
    }

    @Test
    public void should_mark_todo_as_completed() throws Throwable {
        // Given
        final TodoAggregateRoot todoAggregateRoot = mock(TodoAggregateRoot.class);
        final Specification<Todo> canMarkTodoAsCompletedSpecification = mock(Specification.class);
        doReturn(canMarkTodoAsCompletedSpecification).when(todoAggregateRoot).canMarkTodoAsCompletedSpecification();
        doReturn(todoAggregateRoot).when(mockTodoAggregateRootRepository).load("todoId");
        final MarkTodoAsCompletedCommand markTodoAsCompletedCommand = new MarkTodoAsCompletedCommand("todoId");

        // When
        domainMarkTodoAsCompletedCommandHandler.execute(markTodoAsCompletedCommand);

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
        final UnknownTodoException unknownTodoException = assertThrows(UnknownTodoException.class, () -> domainMarkTodoAsCompletedCommandHandler.execute(markTodoAsCompletedCommand));
        assertEquals(new UnknownTodoException("todoId"), unknownTodoException);
    }

}
