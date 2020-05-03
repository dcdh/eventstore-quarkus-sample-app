package com.damdamdeo.todo.domain;

import com.damdamdeo.todo.domain.api.Todo;

public interface TodoRepository {

    Todo get(String todoId);

}
