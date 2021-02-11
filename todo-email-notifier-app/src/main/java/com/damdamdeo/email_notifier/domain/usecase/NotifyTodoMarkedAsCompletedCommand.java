package com.damdamdeo.email_notifier.domain.usecase;

import com.damdamdeo.email_notifier.domain.TodoDomain;

import java.util.Objects;

public final class NotifyTodoMarkedAsCompletedCommand implements UseCaseCommand {

    private final TodoDomain todoDomain;

    public NotifyTodoMarkedAsCompletedCommand(final TodoDomain todoDomain) {
        this.todoDomain = Objects.requireNonNull(todoDomain);
    }

    public TodoDomain todoDomain() {
        return todoDomain;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotifyTodoMarkedAsCompletedCommand)) return false;
        NotifyTodoMarkedAsCompletedCommand that = (NotifyTodoMarkedAsCompletedCommand) o;
        return Objects.equals(todoDomain, that.todoDomain);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoDomain);
    }
}
