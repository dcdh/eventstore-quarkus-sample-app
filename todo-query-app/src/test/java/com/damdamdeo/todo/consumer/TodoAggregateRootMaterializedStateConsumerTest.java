package com.damdamdeo.todo.consumer;

import com.damdamdeo.eventsourced.consumer.api.eventsourcing.AggregateRootMaterializedStateConsumer;
import com.damdamdeo.eventsourced.consumer.infra.eventsourcing.serialization.JacksonAggregateRootMaterializedStateConsumerDeserializer;
import com.damdamdeo.eventsourced.encryption.api.Encryption;
import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.damdamdeo.todo.domain.api.TodoStatus;
import io.quarkus.test.junit.QuarkusTest;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@QuarkusTest
public class TodoAggregateRootMaterializedStateConsumerTest {

    @Inject
    JacksonAggregateRootMaterializedStateConsumerDeserializer jacksonAggregateRootMaterializedStateConsumerDeserializer;

    @Inject
    Encryption encryption;

    @Test
    public void should_verify_equality() {
        EqualsVerifier.forClass(TodoAggregateRootMaterializedStateConsumer.class)
                .withRedefinedSuperclass().verify();
    }

    @Test
    public void should_deserialize() {
        // Given
        final Secret secret = mock(Secret.class);
        doReturn("lorem ipsum").when(secret).decrypt("uWtQHOtmgpaw22nCiexwpg==", encryption);

        // When
        final AggregateRootMaterializedStateConsumer deserialized = jacksonAggregateRootMaterializedStateConsumerDeserializer.deserialize(secret,
                "{\"@type\": \"TodoAggregateRoot\", \"aggregateRootId\": \"todoId\", \"version\":0, \"aggregateRootType\": \"TodoAggregateRoot\", \"description\": \"uWtQHOtmgpaw22nCiexwpg==\", \"todoStatus\": \"IN_PROGRESS\"}");

        // Then
        assertEquals(new TodoAggregateRootMaterializedStateConsumer("todoId", "TodoAggregateRoot", 0l, "lorem ipsum", TodoStatus.IN_PROGRESS), deserialized);
        verify(secret, times(1)).decrypt(any(), any());
    }

}
