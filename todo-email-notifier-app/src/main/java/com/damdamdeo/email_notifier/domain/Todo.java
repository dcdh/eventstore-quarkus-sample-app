package com.damdamdeo.email_notifier.domain;

public interface Todo {

    String todoId();

    String description();

    TodoStatus todoStatus();

    String currentEventId();

    Long version();

}
