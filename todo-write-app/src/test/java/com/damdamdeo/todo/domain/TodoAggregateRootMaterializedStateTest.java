package com.damdamdeo.todo.domain;

import com.damdamdeo.eventsourced.encryption.api.PresentSecret;
import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.JacksonAggregateRootMaterializedStateDeSerializer;
import com.damdamdeo.todo.domain.command.CreateNewTodoCommand;
import com.damdamdeo.todo.domain.api.TodoStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.inject.Inject;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class TodoAggregateRootMaterializedStateTest {

    @Inject
    JacksonAggregateRootMaterializedStateDeSerializer jacksonAggregateRootMaterializedStateDeSerializer;

    @InjectMock
    SecretStore secretStore;

    @Test
    public void should_serialize_encoded() throws Exception {
        // Given
        final ObjectMapper objectMapper = new ObjectMapper();
        final TodoAggregateRoot todoAggregateRoot = new TodoAggregateRoot("todoId");
        todoAggregateRoot.handle(new CreateNewTodoCommand("lorem ipsum"), "todoId");
        doReturn(new PresentSecret("IbXcNPlTEnoPzWVPNwASmPepRVWBHhPN")).when(secretStore).read(any());

        // When
        final JsonNode serialized = jacksonAggregateRootMaterializedStateDeSerializer.serialize(todoAggregateRoot,
                true, objectMapper);
        final String expectedJsonMaterializedState = new Scanner(this.getClass().getResourceAsStream("/expected/todoAggregateRootMaterializedStateEncoded.json"), "UTF-8")
                .useDelimiter("\\A").next();
        JSONAssert.assertEquals(expectedJsonMaterializedState, serialized.toString(), true);
        verify(secretStore, times(1)).read(any());
    }

    @Test
    public void should_serialize_not_encoded() throws Exception {
        // Given
        final ObjectMapper objectMapper = new ObjectMapper();
        final TodoAggregateRoot todoAggregateRoot = new TodoAggregateRoot("todoId");
        todoAggregateRoot.handle(new CreateNewTodoCommand("lorem ipsum"), "todoId");

        // When
        final JsonNode serialized = jacksonAggregateRootMaterializedStateDeSerializer.serialize(todoAggregateRoot,
                false, objectMapper);
        final String expectedJsonMaterializedState = new Scanner(this.getClass().getResourceAsStream("/expected/todoAggregateRootMaterializedStateNotEncoded.json"), "UTF-8")
                .useDelimiter("\\A").next();
        JSONAssert.assertEquals(expectedJsonMaterializedState, serialized.toString(), true);
    }

    @Test
    public void should_deserialize_aggregate_root_from_materialized_state() throws Exception {
        // Given
        final ObjectMapper objectMapper = new ObjectMapper();
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);
        doReturn("aggregateRootId").when(aggregateRootId).aggregateRootId();
        final JsonNode aggregateRoot = objectMapper.readTree("{\n" +
                "  \"description\": \"lorem ipsum\",\n" +
                "  \"todoId\": \"todoId\",\n" +
                "  \"todoStatus\": \"IN_PROGRESS\"\n" +
                "}");

        // When
        final TodoAggregateRoot todoAggregateRoot = jacksonAggregateRootMaterializedStateDeSerializer.deserialize(aggregateRootId, aggregateRoot, 0l);

        // Then
        final TodoAggregateRoot expectedTodoAggregateRoot = TodoAggregateRoot.newBuilder()
                .withAggregateRootId("todoId")
                .withTodoStatus(TodoStatus.IN_PROGRESS)
                .withDescription("lorem ipsum")
                .withVersion(0l)
                .build();
        assertEquals(expectedTodoAggregateRoot, todoAggregateRoot);
        verify(aggregateRootId, times(1)).aggregateRootId();
    }

}
