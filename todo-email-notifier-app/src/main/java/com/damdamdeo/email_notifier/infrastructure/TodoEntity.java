package com.damdamdeo.email_notifier.infrastructure;

import com.damdamdeo.email_notifier.domain.Todo;
import com.damdamdeo.email_notifier.domain.TodoStatus;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class TodoEntity implements Todo {

    @Id
    private String todoId;

    private String description;

    @Enumerated(EnumType.STRING)
    private TodoStatus todoStatus;

    private Long version;

    public TodoEntity() {}

    public TodoEntity(final String todoId,
                      final String description,
                      final TodoStatus todoStatus,
                      final Long version) {
        this.todoId = todoId;
        this.description = description;
        this.todoStatus = todoStatus;
        this.version = version;
    }

    public TodoEntity(final Todo todo) {
        this(todo.todoId(),
                todo.description(),
                todo.todoStatus(),
                todo.version());
    }

    public void markAsCompleted(final Long version) {
        this.todoStatus = TodoStatus.COMPLETED;
        this.version = version;
    }

    @Override
    public String todoId() {
        return todoId;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public TodoStatus todoStatus() {
        return todoStatus;
    }

    @Override
    public Long version() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TodoEntity)) return false;
        TodoEntity that = (TodoEntity) o;
        return Objects.equals(todoId, that.todoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoId);
    }

    @Override
    public String toString() {
        return "TodoEntity{" +
                "todoId='" + todoId + '\'' +
                ", description='" + description + '\'' +
                ", todoStatus=" + todoStatus +
                ", version=" + version +
                '}';
    }
}
