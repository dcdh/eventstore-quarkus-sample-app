package com.damdamdeo.todo.infrastructure.deserializer;

import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.ApiAggregateRootId;
import com.damdamdeo.todo.domain.event.TodoCreatedEventPayload;
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
import static org.mockito.Mockito.*;

@QuarkusTest
public class TodoCreatedEventPayloadTest {

    @Inject
    TodoCreatedEventPayloadDeSerializer todoCreatedEventPayloadDeSerializer;

    @InjectMock
    SecretStore secretStore;

    @Test
    public void should_serialize() throws Exception {
        // Given
        final Secret secret = mock(Secret.class);
        doReturn(secret).when(secretStore).read(any());
        doReturn("encryptedDescription").when(secret).encrypt(any(), any(), any());

        // When
        final JsonObject serialized = todoCreatedEventPayloadDeSerializer.encode(
                new ApiAggregateRootId("todoId", "TodoAggregateRoot"),
                new TodoCreatedEventPayload("todoId", "lorem ipsum"));

        // Then
        final String expectedJsonPayload = new Scanner(this.getClass().getResourceAsStream("/expected/todoCreatedEventPayload.json"), "UTF-8")
                .useDelimiter("\\A").next();
        JSONAssert.assertEquals(expectedJsonPayload, serialized.toString(), true);
        verify(secretStore, times(1)).read(any());
        verify(secret, times(1)).encrypt(any(), any(), any());
    }

    @Test
    public void should_deserialize() throws Exception {
        // Given
        final JsonObject jsonObject = Json.createReader(new StringReader("{\"todoId\": \"todoId\", \"description\": \"lorem ipsum\"}")).readObject();

        // When
        final TodoCreatedEventPayload aggregateRootEventPayload = (TodoCreatedEventPayload) todoCreatedEventPayloadDeSerializer.decode(jsonObject);

        // Then
        assertEquals(new TodoCreatedEventPayload("todoId", "lorem ipsum"), aggregateRootEventPayload);
    }

}
