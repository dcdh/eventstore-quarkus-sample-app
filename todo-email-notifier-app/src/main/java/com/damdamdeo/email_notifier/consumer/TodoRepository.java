package com.damdamdeo.email_notifier.consumer;

import com.damdamdeo.email_notifier.infrastructure.TodoEntity;

public interface TodoRepository {

    TodoEntity find(String todoId);

    void persist(TodoEntity todoEntity);

    TodoEntity merge(TodoEntity todoEntity);

}
