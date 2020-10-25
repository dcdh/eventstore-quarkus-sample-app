package com.damdamdeo.todo.infrastructure.deserializer;

import com.damdamdeo.eventsourced.encryption.api.PresentSecret;
import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import com.damdamdeo.eventsourced.model.api.AggregateRootId;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.JsonbAggregateRootMaterializedStateDeSerializer;
import com.damdamdeo.todo.domain.TodoAggregateRoot;
import com.damdamdeo.todo.domain.api.TodoStatus;
import com.damdamdeo.todo.domain.command.CreateNewTodoCommand;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import java.io.StringReader;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class TodoAggregateRootMaterializedStateTest {

    @Inject
    JsonbAggregateRootMaterializedStateDeSerializer jsonbAggregateRootMaterializedStateDeSerializer;

    @InjectMock
    SecretStore secretStore;

    @Test
    public void should_serialize_encoded() throws Exception {
        // Given
        final TodoAggregateRoot todoAggregateRoot = new TodoAggregateRoot("todoId");
        todoAggregateRoot.handle(new CreateNewTodoCommand("lorem ipsum"), "todoId");
        doReturn(new PresentSecret("IbXcNPlTEnoPzWVPNwASmPepRVWBHhPN")).when(secretStore).read(any());

        // When
        final JsonObject serialized = jsonbAggregateRootMaterializedStateDeSerializer.serialize(todoAggregateRoot,
                true);
        final String expectedJsonMaterializedState = new Scanner(this.getClass().getResourceAsStream("/expected/todoAggregateRootMaterializedStateEncoded.json"), "UTF-8")
                .useDelimiter("\\A").next();
        JSONAssert.assertEquals(expectedJsonMaterializedState, serialized.toString(), true);
        verify(secretStore, times(1)).read(any());
    }

    @Test
    public void should_serialize_not_encoded() throws Exception {
        // Given
        final TodoAggregateRoot todoAggregateRoot = new TodoAggregateRoot("todoId");
        todoAggregateRoot.handle(new CreateNewTodoCommand("lorem ipsum"), "todoId");

        // When
        final JsonObject serialized = jsonbAggregateRootMaterializedStateDeSerializer.serialize(todoAggregateRoot,
                false);
        final String expectedJsonMaterializedState = new Scanner(this.getClass().getResourceAsStream("/expected/todoAggregateRootMaterializedStateNotEncoded.json"), "UTF-8")
                .useDelimiter("\\A").next();
        JSONAssert.assertEquals(expectedJsonMaterializedState, serialized.toString(), true);
    }

    @Test
    public void should_deserialize_aggregate_root_from_materialized_state() throws Exception {
        // Given
        final AggregateRootId aggregateRootId = mock(AggregateRootId.class);
        doReturn("aggregateRootId").when(aggregateRootId).aggregateRootId();

        final JsonObject aggregateRoot = Json.createReader(new StringReader("{\n" +
                "  \"description\": \"lorem ipsum\",\n" +
                "  \"todoId\": \"todoId\",\n" +
                "  \"todoStatus\": \"IN_PROGRESS\"\n" +
                "}")).readObject();

        // When
        final TodoAggregateRoot todoAggregateRoot = jsonbAggregateRootMaterializedStateDeSerializer.deserialize(aggregateRootId, aggregateRoot, 0l);

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
