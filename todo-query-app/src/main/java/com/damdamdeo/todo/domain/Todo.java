package com.damdamdeo.todo.domain;

public interface Todo {

    String todoId();

    String description();

    TodoStatus todoStatus();

    String currentEventId();

    Long version();

}
