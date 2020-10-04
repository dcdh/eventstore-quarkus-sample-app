package com.damdamdeo.todo.domain;

import com.damdamdeo.todo.domain.api.UnknownTodoException;

import java.util.List;

public interface TodoDomainRepository {

    TodoDomain get(String todoId) throws UnknownTodoException;

    TodoDomain persist(TodoDomain todoDomain);

    TodoDomain merge(TodoDomain todoDomain);

    List<TodoDomain> fetchAll();

}
