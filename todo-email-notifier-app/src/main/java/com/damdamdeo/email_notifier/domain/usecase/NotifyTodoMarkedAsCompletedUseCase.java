package com.damdamdeo.email_notifier.domain.usecase;

import com.damdamdeo.email_notifier.domain.EmailNotifier;
import com.damdamdeo.email_notifier.domain.TemplateGenerator;

import java.util.Objects;

public final class NotifyTodoMarkedAsCompletedUseCase implements UseCase<NotifyTodoMarkedAsCompletedCommand, Void> {

    private final TemplateGenerator templateGenerator;
    private final EmailNotifier emailNotifier;

    public NotifyTodoMarkedAsCompletedUseCase(final TemplateGenerator templateGenerator, final EmailNotifier emailNotifier) {
        this.templateGenerator = Objects.requireNonNull(templateGenerator);
        this.emailNotifier = Objects.requireNonNull(emailNotifier);
    }

    @Override
    public Void execute(final NotifyTodoMarkedAsCompletedCommand command) throws UseCaseException {
        final String content = templateGenerator.generateTodoMarkedAsCompleted(command.todoDomain());
        emailNotifier.notify("Todo marked as completed", content);
        return null;
    }

}
