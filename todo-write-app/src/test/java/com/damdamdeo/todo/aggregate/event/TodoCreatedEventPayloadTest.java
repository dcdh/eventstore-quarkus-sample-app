package com.damdamdeo.todo.aggregate.event;

import com.damdamdeo.eventsourced.encryption.api.Secret;
import com.damdamdeo.eventsourced.encryption.api.SecretStore;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.ApiAggregateRootId;
import com.damdamdeo.todo.infrastructure.TodoCreatedEventPayloadDeSerializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import javax.inject.Inject;

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
    public void should_verify_equality() {
        EqualsVerifier.forClass(TodoCreatedEventPayload.class).verify();
    }

    @Test
    public void should_serialize() throws Exception {
        // Given
        final ObjectMapper objectMapper = new ObjectMapper();
        final Secret secret = mock(Secret.class);
        doReturn(secret).when(secretStore).read(any());
        doReturn("encryptedDescription").when(secret).encrypt(any(), any(), any());

        // When
        final JsonNode serialized = todoCreatedEventPayloadDeSerializer.encode(
                new ApiAggregateRootId("todoId", "TodoAggregateRoot"),
                new TodoCreatedEventPayload("todoId", "lorem ipsum"), objectMapper);

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
        final ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode jsonNode = objectMapper.readTree("{\"todoId\": \"todoId\", \"description\": \"lorem ipsum\"}");

        // When
        final TodoCreatedEventPayload aggregateRootEventPayload = (TodoCreatedEventPayload) todoCreatedEventPayloadDeSerializer.decode(jsonNode);

        // Then
        assertEquals(new TodoCreatedEventPayload("todoId", "lorem ipsum"), aggregateRootEventPayload);
    }

}