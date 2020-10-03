package com.damdamdeo.email_notifier.domain;

import java.util.Objects;

public final class TodoDomain {

    private final String todoId;

    private final String description;

    private TodoDomain(final Builder builder) {
        this.todoId = Objects.requireNonNull(builder.todoId);
        this.description = Objects.requireNonNull(builder.description);
    }

    public String todoId() {
        return todoId;
    }

    public String description() {
        return description;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {

        private String todoId;

        private String description;

        public Builder withTodoId(final String todoId) {
            this.todoId = todoId;
            return this;
        }

        public Builder withDescription(final String description) {
            this.description = description;
            return this;
        }

        public TodoDomain build() {
            return new TodoDomain(this);
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TodoDomain)) return false;
        TodoDomain that = (TodoDomain) o;
        return Objects.equals(todoId, that.todoId) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoId, description);
    }
}
