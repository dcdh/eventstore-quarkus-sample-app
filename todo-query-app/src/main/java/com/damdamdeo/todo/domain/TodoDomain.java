package com.damdamdeo.todo.domain;

import com.damdamdeo.todo.domain.api.Todo;
import com.damdamdeo.todo.domain.api.TodoStatus;

import java.util.Objects;

public final class TodoDomain implements Todo {

    private final String todoId;

    private final String description;

    private final TodoStatus todoStatus;

    private final Long version;

    private TodoDomain(final Builder builder) {
        this.todoId = Objects.requireNonNull(builder.todoId);
        this.description = Objects.requireNonNull(builder.description);
        this.todoStatus = Objects.requireNonNull(builder.todoStatus);
        this.version = Objects.requireNonNull(builder.version);
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public TodoDomain markAsCompleted(final Long version) {
        return newBuilder()
                .withTodoId(todoId)
                .withDescription(description)
                .withTodoStatus(TodoStatus.COMPLETED)
                .withVersion(version)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TodoDomain)) return false;
        TodoDomain that = (TodoDomain) o;
        return Objects.equals(todoId, that.todoId) &&
                Objects.equals(description, that.description) &&
                todoStatus == that.todoStatus &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoId, description, todoStatus, version);
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

        public TodoDomain build() {
            return new TodoDomain(this);
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

}
