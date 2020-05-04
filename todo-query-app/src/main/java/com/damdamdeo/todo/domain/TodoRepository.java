package com.damdamdeo.todo.domain;

import com.damdamdeo.todo.domain.api.Todo;

import java.util.List;

public interface TodoRepository {

    Todo get(String todoId);

    List<Todo> fetchAll();

}
