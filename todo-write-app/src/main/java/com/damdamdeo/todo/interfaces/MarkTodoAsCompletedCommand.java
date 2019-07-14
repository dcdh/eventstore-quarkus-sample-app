package com.damdamdeo.todo.interfaces;

import com.damdamdeo.todo.api.Todo;
import com.damdamdeo.todo.api.TodoAggregateRootRepository;
import com.damdamdeo.todo.domain.TodoAggregateRoot;
import com.damdamdeo.todo.domain.TodoCommand;
import com.damdamdeo.todo.domain.event.TodoMarkedAsCompletedEventPayload;

import java.util.Objects;

public class MarkTodoAsCompletedCommand implements TodoCommand {

    private String todoId;

    public String getTodoId() {
        return todoId;
    }

    public void setTodoId(String todoId) {
        this.todoId = todoId;
    }

    @Override
    public String todoId() {
        return todoId;
    }

    @Override
    public boolean exactlyOnceCommandExecution() {
        return false;
    }

    @Override
    public Todo handle(final TodoAggregateRootRepository todoAggregateRootRepository) {
        final TodoAggregateRoot todoAggregateRoot = todoAggregateRootRepository.load(todoId);
        todoAggregateRoot.apply(new TodoMarkedAsCompletedEventPayload(todoId));
        return todoAggregateRootRepository.save(todoAggregateRoot);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarkTodoAsCompletedCommand that = (MarkTodoAsCompletedCommand) o;
        return Objects.equals(todoId, that.todoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoId);
    }
}
