package com.damdamdeo.todo.publicfrontend.interfaces;

import com.damdamdeo.todo.publicfrontend.domain.TodoStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Objects;

@RegisterForReflection
@JsonIgnoreProperties(ignoreUnknown = true)
public final class TodoDTO {

    public final String todoId;

    public final String description;

    public final TodoStatus todoStatus;

    public final Boolean canMarkTodoAsCompleted;

    public final Long version;

    public TodoDTO(@JsonProperty("todoId") final String todoId,
                   @JsonProperty("description") final String description,
                   @JsonProperty("todoStatus") final TodoStatus todoStatus,
                   @JsonProperty("canMarkTodoAsCompleted") final Boolean canMarkTodoAsCompleted,
                   @JsonProperty("version") final Long version) {
        this.todoId = todoId;
        this.description = description;
        this.todoStatus = todoStatus;
        this.canMarkTodoAsCompleted = canMarkTodoAsCompleted;
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TodoDTO todoDTO = (TodoDTO) o;
        return Objects.equals(todoId, todoDTO.todoId) &&
                Objects.equals(description, todoDTO.description) &&
                todoStatus == todoDTO.todoStatus &&
                Objects.equals(canMarkTodoAsCompleted, todoDTO.canMarkTodoAsCompleted) &&
                Objects.equals(version, todoDTO.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoId, description, todoStatus, canMarkTodoAsCompleted, version);
    }
}
