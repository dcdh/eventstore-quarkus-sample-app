package com.damdamdeo.todo.domain;

import com.damdamdeo.todo.aggregate.TodoAggregateRoot;
import com.damdamdeo.todo.api.TodoStatus;
import com.damdamdeo.todo.user.type.DefaultAggregateRootAdapter;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TodoAggregateRootProjectionTest {

    private static final Jsonb MAPPER = JsonbBuilder.create(new JsonbConfig()
            .withFormatting(true)
            .withAdapters(new DefaultAggregateRootAdapter()));

    @Test
    public void should_serialize() throws JSONException {
        // Given
        final TodoAggregateRoot todoAggregateRoot = new TodoAggregateRoot("todoId",
                "lorem ipsum",
                TodoStatus.IN_PROGRESS,
                1L);

        // When
        final String json = MAPPER.toJson(todoAggregateRoot);

        // Then
        JSONAssert.assertEquals(
                "{\"@aggregaterootType\": \"TodoAggregateRoot\", \"aggregateRootId\": \"todoId\", \"description\": \"lorem ipsum\", \"todoStatus\": \"IN_PROGRESS\", \"version\": 1}", json, JSONCompareMode.STRICT);
    }

    @Test
    public void should_deserialize() {
        // Given
        final String json = "{\"@aggregaterootType\": \"TodoAggregateRoot\", \"aggregateRootId\": \"todoId\", \"description\": \"lorem ipsum\", \"todoStatus\": \"IN_PROGRESS\", \"version\": 1}";

        // When
        final TodoAggregateRoot todoAggregateRoot = MAPPER.fromJson(json, TodoAggregateRoot.class);

        // Then
        assertEquals("todoId", todoAggregateRoot.aggregateRootId());
        assertEquals("todoId", todoAggregateRoot.todoId());
        assertEquals("lorem ipsum", todoAggregateRoot.description());
        assertEquals(TodoStatus.IN_PROGRESS, todoAggregateRoot.todoStatus());
        assertEquals(1l, todoAggregateRoot.version());
    }

}
