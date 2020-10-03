package com.damdamdeo.email_notifier.infrastructure.template_generator;

import com.damdamdeo.email_notifier.domain.TemplateGenerator;
import com.damdamdeo.email_notifier.domain.TodoDomain;
import io.quarkus.qute.Template;
import io.quarkus.qute.api.ResourcePath;

import javax.enterprise.context.ApplicationScoped;
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
    public String generateTodoCreated(final TodoDomain todoDomainCreated) {
        return todoCreatedTemplate.data("todoId", todoDomainCreated.todoId())
                .data("description", todoDomainCreated.description())
                .render();
    }

    @Override
    public String generateTodoMarkedAsCompleted(final TodoDomain todoDomainMarkedAsCompleted) {
        return todoMarkedAsCompletedTemplate.data("todoId", todoDomainMarkedAsCompleted.todoId())
                .data("description", todoDomainMarkedAsCompleted.description())
                .render();
    }
}
