package com.damdamdeo.todo.publicfrontend.interfaces;

import com.damdamdeo.todo.publicfrontend.domain.TodoStatus;
import io.quarkus.runtime.annotations.RegisterForReflection;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;
import java.util.Objects;

@RegisterForReflection
public final class TodoDTO {

    public final String todoId;

    public final String description;

    public final TodoStatus todoStatus;

    public final Boolean canMarkTodoAsCompleted;

    public final Long version;

    @JsonbCreator
    public TodoDTO(@JsonbProperty("todoId") final String todoId,
                   @JsonbProperty("description") final String description,
                   @JsonbProperty("todoStatus") final TodoStatus todoStatus,
                   @JsonbProperty("canMarkTodoAsCompleted") final Boolean canMarkTodoAsCompleted,
                   @JsonbProperty("version") final Long version) {
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
