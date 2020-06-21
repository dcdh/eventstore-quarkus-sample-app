package com.damdamdeo.todo.consumer;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootMaterializedStateConsumer;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.JacksonAggregateRootMaterializedStateConsumerDeserializer;
import com.damdamdeo.eventsourced.model.api.AggregateRootSecret;
import com.damdamdeo.todo.consumer.TodoAggregateRootMaterializedStateConsumer;
import com.damdamdeo.todo.domain.api.TodoStatus;
import io.quarkus.test.junit.QuarkusTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@QuarkusTest
public class TodoAggregateRootMaterializedStateConsumerTest {

    @Inject
    JacksonAggregateRootMaterializedStateConsumerDeserializer jacksonAggregateRootMaterializedStateConsumerDeserializer;

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(TodoAggregateRootMaterializedStateConsumer.class)
                .withRedefinedSuperclass().verify();
    }

    @Test
    public void should_deserialize() {
        // Given
        final AggregateRootSecret aggregateRootSecret = mock(AggregateRootSecret.class);
        doReturn("AAlwSnNqyIRebwRqBfHufaCTXoRFRllg").when(aggregateRootSecret).secret();

        // When
        final AggregateRootMaterializedStateConsumer deserialized = jacksonAggregateRootMaterializedStateConsumerDeserializer.deserialize(Optional.of(aggregateRootSecret),
                "{\"@type\": \"TodoAggregateRoot\", \"aggregateRootId\": \"todoId\", \"version\":0, \"aggregateRootType\": \"TodoAggregateRoot\", \"description\": \"uWtQHOtmgpaw22nCiexwpg==\", \"todoStatus\": \"IN_PROGRESS\"}");

        // Then
        assertEquals(new TodoAggregateRootMaterializedStateConsumer("todoId", "TodoAggregateRoot", 0l, "lorem ipsum", TodoStatus.IN_PROGRESS), deserialized);
        verify(aggregateRootSecret, times(1)).secret();
    }

}
