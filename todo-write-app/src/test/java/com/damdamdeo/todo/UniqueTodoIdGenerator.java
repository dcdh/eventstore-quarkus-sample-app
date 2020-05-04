package com.damdamdeo.todo;

import com.damdamdeo.todo.command.handler.TodoIdGenerator;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

@Alternative
@Priority(1)
@ApplicationScoped
public class UniqueTodoIdGenerator implements TodoIdGenerator {

    @Override
    public String generateTodoId() {
        return "todoId";
    }

}
