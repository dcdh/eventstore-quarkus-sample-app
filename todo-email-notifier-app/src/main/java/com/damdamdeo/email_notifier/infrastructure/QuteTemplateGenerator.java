package com.damdamdeo.email_notifier.infrastructure;

import com.damdamdeo.email_notifier.domain.TemplateGenerator;
import com.damdamdeo.email_notifier.domain.TodoCreated;
import com.damdamdeo.email_notifier.domain.TodoMarkedAsCompleted;
import io.quarkus.qute.Template;
import io.quarkus.qute.api.ResourcePath;

import javax.enterprise.context.Dependent;
import java.io.IOException;
import java.util.Objects;

@Dependent
public class QuteTemplateGenerator implements TemplateGenerator {

    final Template todoCreatedTemplate;

    final Template todoMarketAsCompletedTemplate;

    public QuteTemplateGenerator(@ResourcePath("todoCreated.html") final Template todoCreatedTemplate,
                                 @ResourcePath("todoMarketAsCompleted.html") final Template todoMarketAsCompletedTemplate) {
        this.todoCreatedTemplate = Objects.requireNonNull(todoCreatedTemplate);
        this.todoMarketAsCompletedTemplate = Objects.requireNonNull(todoMarketAsCompletedTemplate);
    }

    @Override
    public String generate(final TodoCreated todoCreated) throws IOException {
        return todoCreatedTemplate.data("todoId", todoCreated.todoId())
                .data("description", todoCreated.description())
                .render();
    }

    @Override
    public String generate(final TodoMarkedAsCompleted todoMarkedAsCompleted) throws IOException {
        return todoMarketAsCompletedTemplate.data("todoId", todoMarkedAsCompleted.todoId())
                .data("description", todoMarkedAsCompleted.description())
                .render();
    }

}
