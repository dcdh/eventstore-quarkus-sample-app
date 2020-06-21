package com.damdamdeo.todo.aggregate;

import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.JacksonAggregateRootMaterializedStateSerializer;
import com.damdamdeo.todo.command.CreateNewTodoCommand;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@QuarkusTest
public class TodoAggregateRootMaterializedStateTest {

    @Inject
    JacksonAggregateRootMaterializedStateSerializer jacksonAggregateRootMaterializedStateSerializer;

    @Test
    public void should_serialize() {
        // Given
        final TodoAggregateRoot todoAggregateRoot = new TodoAggregateRoot();
        todoAggregateRoot.handle(new CreateNewTodoCommand("lorem ipsum"), "todoId");

        // When
        final String serialized = jacksonAggregateRootMaterializedStateSerializer.serialize(todoAggregateRoot);

        // Then
        assertEquals("{\"@type\":\"TodoAggregateRoot\",\"aggregateRootId\":\"todoId\",\"aggregateRootType\":\"TodoAggregateRoot\",\"version\":0,\"description\":\"lorem ipsum\",\"todoStatus\":\"IN_PROGRESS\"}", serialized);
    }

    @Test
    public void should_serialize_using_encryption() {
        // Given
        final AggregateRootSecret aggregateRootSecret = mock(AggregateRootSecret.class);
        doReturn("AAlwSnNqyIRebwRqBfHufaCTXoRFRllg").when(aggregateRootSecret).secret();
        final TodoAggregateRoot todoAggregateRoot = new TodoAggregateRoot();
        todoAggregateRoot.handle(new CreateNewTodoCommand("lorem ipsum"), "todoId");

        // When
        final String serialized = jacksonAggregateRootMaterializedStateSerializer.serialize(Optional.of(aggregateRootSecret), todoAggregateRoot);

        // Then
        assertEquals("{\"@type\":\"TodoAggregateRoot\",\"aggregateRootId\":\"todoId\",\"aggregateRootType\":\"TodoAggregateRoot\",\"version\":0,\"description\":\"uWtQHOtmgpaw22nCiexwpg==\",\"todoStatus\":\"IN_PROGRESS\"}", serialized);
        verify(aggregateRootSecret, times(1)).secret();
    }

}
