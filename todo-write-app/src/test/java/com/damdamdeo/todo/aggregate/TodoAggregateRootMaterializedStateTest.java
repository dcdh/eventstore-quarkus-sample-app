package com.damdamdeo.todo.aggregate;

import com.damdamdeo.eventsourced.encryption.api.PresentSecret;
import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import com.damdamdeo.eventsourced.mutable.infra.eventsourcing.serialization.JacksonAggregateRootMaterializedStateSerializer;
import com.damdamdeo.todo.command.CreateNewTodoCommand;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.inject.Inject;

import java.util.Scanner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@QuarkusTest
public class TodoAggregateRootMaterializedStateTest {

    @Inject
    JacksonAggregateRootMaterializedStateSerializer jacksonAggregateRootMaterializedStateSerializer;

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
        final JsonNode serialized = jacksonAggregateRootMaterializedStateSerializer.encode(todoAggregateRoot,
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
        final JsonNode serialized = jacksonAggregateRootMaterializedStateSerializer.encode(todoAggregateRoot,
                false, objectMapper);
        final String expectedJsonMaterializedState = new Scanner(this.getClass().getResourceAsStream("/expected/todoAggregateRootMaterializedStateNotEncoded.json"), "UTF-8")
                .useDelimiter("\\A").next();
        JSONAssert.assertEquals(expectedJsonMaterializedState, serialized.toString(), true);
    }

}
