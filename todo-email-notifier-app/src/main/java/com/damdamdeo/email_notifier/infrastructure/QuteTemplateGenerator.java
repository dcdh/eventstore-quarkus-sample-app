package com.damdamdeo.email_notifier.infrastructure;

import com.damdamdeo.email_notifier.domain.TemplateGenerator;
import com.damdamdeo.email_notifier.domain.Todo;
import io.quarkus.qute.Template;
import io.quarkus.qute.api.ResourcePath;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.util.Objects;

@ApplicationScoped
public class QuteTemplateGenerator implements TemplateGenerator {

    final Template todoCreatedTemplate;

    final Template todoMarkedAsCompletedTemplate;

    public QuteTemplateGenerator(@ResourcePath("todoCreated.html") final Template todoCreatedTemplate,
                                 @ResourcePath("todoMarkedAsCompleted.html") final Template todoMarkedAsCompletedTemplate) {
        this.todoCreatedTemplate = Objects.requireNonNull(todoCreatedTemplate);
        this.todoMarkedAsCompletedTemplate = Objects.requireNonNull(todoMarkedAsCompletedTemplate);
    }

    @Override
    public String generateTodoCreated(final Todo todoCreated) throws IOException {
        return todoCreatedTemplate.data("todoId", todoCreated.todoId())
                .data("description", todoCreated.description())
                .render();
    }

    @Override
    public String generateTodoMarkedAsCompleted(final Todo todoMarkedAsCompleted) throws IOException {
        return todoMarkedAsCompletedTemplate.data("todoId", todoMarkedAsCompleted.todoId())
                .data("description", todoMarkedAsCompleted.description())
                .render();
    }
}
