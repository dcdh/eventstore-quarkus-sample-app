package com.damdamdeo.todo.interfaces;

import com.damdamdeo.todo.domain.api.Todo;
import com.damdamdeo.todo.domain.api.TodoStatus;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class TodoDTO {

    public String todoId;

    public String description;

    public TodoStatus todoStatus;

    public Boolean canMarkTodoAsCompleted;

    public Long version;

    public TodoDTO() {}

    public TodoDTO(final Todo todo) {
        this.todoId = todo.todoId();
        this.description = todo.description();
        this.todoStatus = todo.todoStatus();
        this.canMarkTodoAsCompleted = todo
                .canMarkTodoAsCompletedSpecification()
                .isSatisfiedBy(todo);
        this.version = todo.version();
    }

    public String getTodoId() {
        return todoId;
    }

    public void setTodoId(String todoId) {
        this.todoId = todoId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TodoStatus getTodoStatus() {
        return todoStatus;
    }

    public void setTodoStatus(TodoStatus todoStatus) {
        this.todoStatus = todoStatus;
    }

    public Boolean getCanMarkTodoAsCompleted() {
        return canMarkTodoAsCompleted;
    }

    public void setCanMarkTodoAsCompleted(Boolean canMarkTodoAsCompleted) {
        this.canMarkTodoAsCompleted = canMarkTodoAsCompleted;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
