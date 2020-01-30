package com.damdamdeo.todo.aggregate;

import com.damdamdeo.todo.domain.api.Todo;

public interface TodoRepository {

    Todo get(String todoId);

}
