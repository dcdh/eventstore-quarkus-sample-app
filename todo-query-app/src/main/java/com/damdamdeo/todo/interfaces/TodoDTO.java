package com.damdamdeo.todo.interfaces;

import com.damdamdeo.todo.domain.Todo;
import com.damdamdeo.todo.domain.TodoStatus;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class TodoDTO {

    public String todoId;

    public String description;

    public TodoStatus todoStatus;

    public Long version;

    public TodoDTO() {}

    public TodoDTO(final Todo todo) {
        this.todoId = todo.todoId();
        this.description = todo.description();
        this.todoStatus = todo.todoStatus();
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

}
