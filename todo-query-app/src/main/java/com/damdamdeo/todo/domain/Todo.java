package com.damdamdeo.todo.domain;

public interface Todo {

    String todoId();

    String description();

    TodoStatus todoStatus();

    Long version();

}
