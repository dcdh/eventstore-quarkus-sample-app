package com.damdamdeo.email_notifier.facade;

import com.damdamdeo.email_notifier.domain.EmailNotifier;
import com.damdamdeo.email_notifier.domain.TemplateGenerator;
import com.damdamdeo.email_notifier.domain.TodoCreated;
import com.damdamdeo.email_notifier.domain.TodoStatus;
import com.damdamdeo.email_notifier.infrastructure.TodoEntity;
import io.vertx.core.json.JsonObject;

import javax.persistence.EntityManager;
import java.io.IOException;

public class TodoCreatedEvent {

    private final String todoId;
    private final String description;

    public TodoCreatedEvent(final JsonObject jsonObject) {
        this.todoId = jsonObject.getString("todoId");
        this.description = jsonObject.getString("description");
    }

    public void handle(final Long version,
            final EntityManager em,
            final TemplateGenerator templateGenerator,
            final EmailNotifier emailNotifier) throws IOException {
        final TodoEntity todoToCreate = new TodoEntity(todoId, description, TodoStatus.IN_PROGRESS, version);
        em.merge(todoToCreate);
        final String content = templateGenerator.generate(new TodoCreated() {
            @Override
            public String todoId() {
                return todoToCreate.todoId();
            }

            @Override
            public String description() {
                return todoToCreate.description();
            }
        });
        emailNotifier.notify(content, "New Todo created");
    }

}
