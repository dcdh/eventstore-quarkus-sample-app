package com.damdamdeo.email_notifier.facade;

import com.damdamdeo.email_notifier.domain.*;
import com.damdamdeo.email_notifier.infrastructure.TodoEntity;
import io.vertx.core.json.JsonObject;

import javax.persistence.EntityManager;
import java.io.IOException;

public class TodoMarkedAsCompletedEvent {

    private final String todoId;

    public TodoMarkedAsCompletedEvent(final JsonObject jsonObject) {
        this.todoId = jsonObject.getString("todoId");
    }

    public void handle(final Long version,
                       final EntityManager em,
                       final TemplateGenerator templateGenerator,
                       final EmailNotifier emailNotifier) throws IOException {
        final TodoEntity todoToMarkAsCompleted = em.find(TodoEntity.class, todoId);
        todoToMarkAsCompleted.markAsCompleted(version);
        em.merge(todoToMarkAsCompleted);
        final String content = templateGenerator.generate(new TodoMarkedAsCompleted() {
            @Override
            public String todoId() {
                return todoToMarkAsCompleted.todoId();
            }

            @Override
            public String description() {
                return todoToMarkAsCompleted.description();
            }
        });
        emailNotifier.notify(content, "Todo marked as completed");
    }

}
