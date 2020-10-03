package com.damdamdeo.email_notifier.domain;

import java.util.Objects;

public final class DomainTodoMarkedAsCompletedNotifierService implements TodoMarkedAsCompletedNotifierService {

    private final TemplateGenerator templateGenerator;
    private final EmailNotifier emailNotifier;

    public DomainTodoMarkedAsCompletedNotifierService(final TemplateGenerator templateGenerator, final EmailNotifier emailNotifier) {
        this.templateGenerator = Objects.requireNonNull(templateGenerator);
        this.emailNotifier = Objects.requireNonNull(emailNotifier);
    }

    @Override
    public void notify(final TodoDomain todoDomain) {
        final String content = templateGenerator.generateTodoMarkedAsCompleted(todoDomain);
        emailNotifier.notify("Todo marked as completed", content);
    }

}
