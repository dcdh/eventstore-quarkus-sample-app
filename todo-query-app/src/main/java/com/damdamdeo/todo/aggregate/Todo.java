package com.damdamdeo.todo.aggregate;

public interface Todo {

    String todoId();

    String description();

    TodoStatus todoStatus();

    String currentEventId();

    Long version();

}
