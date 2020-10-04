package com.damdamdeo.todo.domain;

public interface CreateTodoService {

    // I should use an interface instead. However two variables are fines.
    TodoDomain createTodo(String todoId, String description, Long version);

}
