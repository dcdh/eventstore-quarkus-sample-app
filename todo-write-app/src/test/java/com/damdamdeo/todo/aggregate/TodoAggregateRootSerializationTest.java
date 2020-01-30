package com.damdamdeo.todo.aggregate;

import com.damdamdeo.eventdataspreader.debeziumeventconsumer.infrastructure.spi.JacksonSubtype;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRoot;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.JacksonAggregateRootSerializer;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.spi.JacksonAggregateRootSubtypes;
import com.damdamdeo.eventdataspreader.eventsourcing.api.EncryptedEventSecret;
import com.damdamdeo.todo.command.CreateNewTodoCommand;
import org.junit.jupiter.api.Test;

import java.util.Date;
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

    private static class DefaultEncryptedEventSecret implements EncryptedEventSecret {

        @Override
        public String aggregateRootId() {
            return null;
        }

        @Override
        public String aggregateRootType() {
            return null;
        }

        @Override
        public Date creationDate() {
            return null;
        }

        @Override
        public String secret() {
            return null;
        }

    }

    @Test
    public void should_serialize() {
        // Given
        final AggregateRootSerializer aggregateRootSerializer = new JacksonAggregateRootSerializer(new DefaultJacksonAggregateRootSubtypes());
        final TodoAggregateRoot todoAggregateRoot = new TodoAggregateRoot();
        todoAggregateRoot.handle(new CreateNewTodoCommand("todoId", "lorem ipsum"));

        // When
        final String serialized = aggregateRootSerializer.serialize(new DefaultEncryptedEventSecret(), todoAggregateRoot);

        // Then
        assertEquals("{\"@type\":\"TodoAggregateRoot\",\"aggregateRootId\":\"todoId\",\"description\":\"lorem ipsum\",\"todoStatus\":\"IN_PROGRESS\",\"version\":0,\"aggregateRootType\":\"TodoAggregateRoot\"}", serialized);
    }

}
