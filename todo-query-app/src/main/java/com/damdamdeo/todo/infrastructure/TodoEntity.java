package com.damdamdeo.todo.infrastructure;

import com.damdamdeo.todo.domain.api.Todo;
import com.damdamdeo.todo.domain.api.TodoStatus;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Audited
public class TodoEntity implements Todo {

    @Id
    private String todoId;

    private String description;

    @Enumerated(EnumType.STRING)
    private TodoStatus todoStatus;

    private String currentEventId;

    private Long version;

    public TodoEntity() {}

    public TodoEntity(final String todoId,
                      final String description,
                      final TodoStatus todoStatus,
                      final String currentEventId,
                      final Long version) {
        this.todoId = todoId;
        this.description = description;
        this.todoStatus = todoStatus;
        this.currentEventId = currentEventId;
        this.version = version;
    }

    public void markAsCompleted(final String currentEventId, final Long version) {
        this.todoStatus = TodoStatus.COMPLETED;
        this.currentEventId = currentEventId;
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

    public String currentEventId() {
        return currentEventId;
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
                ", currentEventId='" + currentEventId + '\'' +
                ", version=" + version +
                '}';
    }
}
