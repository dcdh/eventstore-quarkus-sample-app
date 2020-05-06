package com.damdamdeo.todo.interfaces;

import com.damdamdeo.todo.domain.api.Todo;
import com.damdamdeo.todo.domain.api.TodoStatus;
import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Objects;

@RegisterForReflection
public final class TodoDTO {

    public final String todoId;

    public final String description;

    public final TodoStatus todoStatus;

    public final Boolean canMarkTodoAsCompleted;

    public final Long version;

    public TodoDTO(final Todo todo) {
        this.todoId = todo.todoId();
        this.description = todo.description();
        this.todoStatus = todo.todoStatus();
        this.canMarkTodoAsCompleted = todo
                .canMarkTodoAsCompletedSpecification()
                .isSatisfiedBy(todo);
        this.version = todo.version();
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
