package com.damdamdeo.email_notifier.domain;

public interface TodoRepository {

    Todo merge(Todo todo);

    Todo get(String todoId);

}
