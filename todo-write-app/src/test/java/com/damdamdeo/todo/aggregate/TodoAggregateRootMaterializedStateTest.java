package com.damdamdeo.todo.aggregate;

import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.JacksonAggregateRootMaterializedStateSerializer;
import com.damdamdeo.todo.command.CreateNewTodoCommand;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@QuarkusTest
public class TodoAggregateRootMaterializedStateTest {

    @Inject
    JacksonAggregateRootMaterializedStateSerializer jacksonAggregateRootMaterializedStateSerializer;

    @Inject
    Encryption encryption;

    @Test
    public void should_serialize() {
        // Given
        final Secret secret = mock(Secret.class);
        doReturn("uWtQHOtmgpaw22nCiexwpg==").when(secret).encrypt("lorem ipsum", encryption);
        final TodoAggregateRoot todoAggregateRoot = new TodoAggregateRoot();
        todoAggregateRoot.handle(new CreateNewTodoCommand("lorem ipsum"), "todoId");

        // When
        final String serialized = jacksonAggregateRootMaterializedStateSerializer.serialize(secret, todoAggregateRoot);

        // Then
        assertEquals("{\"@type\":\"TodoAggregateRoot\",\"aggregateRootId\":\"todoId\",\"aggregateRootType\":\"TodoAggregateRoot\",\"version\":0,\"description\":\"uWtQHOtmgpaw22nCiexwpg==\",\"todoStatus\":\"IN_PROGRESS\"}", serialized);
        verify(secret, times(1)).encrypt(any(), any());
    }

}
