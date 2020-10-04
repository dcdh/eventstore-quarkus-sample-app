package com.damdamdeo.todo.domain;

public interface MarkTodoAsCompletedService {

    TodoDomain markTodoAsCompleted(String todoId, Long version);

}
