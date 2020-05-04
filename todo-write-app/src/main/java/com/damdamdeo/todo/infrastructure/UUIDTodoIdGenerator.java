package com.damdamdeo.todo.infrastructure;

import com.damdamdeo.todo.command.handler.TodoIdGenerator;

import javax.enterprise.context.ApplicationScoped;
import java.util.UUID;

@ApplicationScoped
public class UUIDTodoIdGenerator implements TodoIdGenerator {

    @Override
    public String generateTodoId() {
        return UUID.randomUUID().toString();
    }

}
