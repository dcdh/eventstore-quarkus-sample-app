package com.damdamdeo.todo.domain;

public interface TodoRepository {

    Todo merge(Todo todo);

    Todo get(String todoId);

}
