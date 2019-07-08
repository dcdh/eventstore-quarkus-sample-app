package com.damdamdeo.todo.interfaces;

import com.damdamdeo.todo.api.Todo;
import com.damdamdeo.todo.api.TodoAggregateRootRepository;
import com.damdamdeo.todo.api.TodoIdAlreadyAffectedException;
import com.damdamdeo.todo.domain.TodoAggregateRoot;
import com.damdamdeo.todo.domain.TodoCommand;
import com.damdamdeo.todo.domain.event.TodoCreatedEventPayload;

import java.util.Objects;

public class CreateNewTodoCommand implements TodoCommand {

    private String todoId;

    private String description;

    public String getTodoId() {
        return todoId;
    }

    public void setTodoId(String todoId) {
        this.todoId = todoId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateNewTodoCommand that = (CreateNewTodoCommand) o;
        return Objects.equals(todoId, that.todoId) &&
                Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(todoId, description);
    }

    @Override
    public String todoId() {
        return null;
    }

    @Override
    public boolean exactlyOnceCommandExecution() {
        return true;
    }

    @Override
    public Todo handle(final TodoAggregateRootRepository todoAggregateRootRepository) {
        if (todoAggregateRootRepository.isTodoIdAffected(todoId)) {
            throw new TodoIdAlreadyAffectedException(todoId);
        }
        final TodoAggregateRoot todoAggregateRoot = new TodoAggregateRoot();
        todoAggregateRoot.apply(new TodoCreatedEventPayload(todoId,
                description));
        return todoAggregateRootRepository.save(todoAggregateRoot);
    }

}
