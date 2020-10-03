package com.damdamdeo.email_notifier.domain;

import java.util.Objects;

public final class DomainTodoCreatedNotifierService implements TodoCreatedNotifierService {

    private final TemplateGenerator templateGenerator;
    private final EmailNotifier emailNotifier;

    public DomainTodoCreatedNotifierService(final TemplateGenerator templateGenerator, final EmailNotifier emailNotifier) {
        this.templateGenerator = Objects.requireNonNull(templateGenerator);
        this.emailNotifier = Objects.requireNonNull(emailNotifier);
    }

    @Override
    public void notify(final TodoDomain todoDomain) {
        final String content = templateGenerator.generateTodoCreated(todoDomain);
        emailNotifier.notify("New Todo created", content);
    }

}
