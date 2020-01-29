package com.damdamdeo.todo.aggregate;

public interface TodoRepository {

    Todo merge(Todo todo);

    Todo get(String todoId);

}
