package com.damdamdeo.email_notifier.facade;

import com.damdamdeo.email_notifier.domain.*;
import com.damdamdeo.email_notifier.infrastructure.TodoEntity;
import io.vertx.core.json.JsonObject;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.concurrent.CompletionStage;

public class TodoMarkedAsCompletedEvent implements TodoEvent {

    private final String todoId;

    public TodoMarkedAsCompletedEvent(final String eventPayload) {
        final JsonObject jsonObject = new JsonObject(eventPayload);
        this.todoId = jsonObject.getString("todoId");
    }

    @Override
    public CompletionStage<Void> handle(final String eventId,
                                        final Long version,
                                        final EntityManager em,
                                        final TemplateGenerator templateGenerator,
                                        final EmailNotifier emailNotifier) throws IOException {
        final TodoEntity todoToMarkAsCompleted = em.find(TodoEntity.class, todoId);
        todoToMarkAsCompleted.markAsCompleted(eventId, version);
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
        return emailNotifier.notify("Todo marked as completed", content);
    }

}
