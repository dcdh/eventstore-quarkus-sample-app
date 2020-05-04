package com.damdamdeo.todo.aggregate;

import com.damdamdeo.eventdataspreader.event.infrastructure.spi.JacksonSubtype;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.JacksonAggregateRootSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.spi.JacksonAggregateRootSubtypes;
import com.damdamdeo.todo.command.CreateNewTodoCommand;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TodoAggregateRootSerializationTest {

    private static class DefaultJacksonAggregateRootSubtypes implements JacksonAggregateRootSubtypes {

        @Override
        public List<JacksonSubtype<AggregateRoot>> jacksonSubtypes() {
            return singletonList(new JacksonSubtype<>(TodoAggregateRoot.class, "TodoAggregateRoot"));
        }

    }

    @Test
    public void should_serialize() {
        // Given
        final AggregateRootSerializer aggregateRootSerializer = new JacksonAggregateRootSerializer(new DefaultJacksonAggregateRootSubtypes());
        final TodoAggregateRoot todoAggregateRoot = new TodoAggregateRoot();
        todoAggregateRoot.handle(new CreateNewTodoCommand("lorem ipsum"), "todoId");

        // When
        final String serialized = aggregateRootSerializer.serialize(todoAggregateRoot);

        // Then
        assertEquals("{\"@type\":\"TodoAggregateRoot\",\"aggregateRootId\":\"todoId\",\"description\":\"lorem ipsum\",\"todoStatus\":\"IN_PROGRESS\",\"version\":0,\"aggregateRootType\":\"TodoAggregateRoot\"}", serialized);
    }

}
