package com.damdamdeo.todo.api;

public interface Todo {

    String todoId();

    String description();

    TodoStatus todoStatus();

    Long version();

}
