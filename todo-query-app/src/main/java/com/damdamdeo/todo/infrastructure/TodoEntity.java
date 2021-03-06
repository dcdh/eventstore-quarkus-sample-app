package com.damdamdeo.todo.infrastructure;

import com.damdamdeo.todo.domain.TodoDomain;
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

    private Long version;

    public TodoEntity() {}

    private TodoEntity(final Builder builder) {
        this.todoId = Objects.requireNonNull(builder.todoId);
        this.description = Objects.requireNonNull(builder.description);
        this.todoStatus = Objects.requireNonNull(builder.todoStatus);
        this.version = Objects.requireNonNull(builder.version);
    }

    public TodoDomain toDomain() {
        return TodoDomain.newBuilder()
                .withTodoId(todoId)
                .withDescription(description)
                .withTodoStatus(todoStatus)
                .withVersion(version)
                .build();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private String todoId;

        private String description;

        private TodoStatus todoStatus;

        private Long version;

        public Builder withTodoId(final String todoId) {
            this.todoId = todoId;
            return this;
        }

        public Builder withDescription(final String description) {
            this.description = description;
            return this;
        }

        public Builder withTodoStatus(final TodoStatus todoStatus) {
            this.todoStatus = todoStatus;
            return this;
        }

        public Builder withVersion(final Long version) {
            this.version = version;
            return this;
        }

        public TodoEntity build() {
            return new TodoEntity(this);
        }

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
