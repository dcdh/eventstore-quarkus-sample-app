package com.damdamdeo.email_notifier.domain.usecase;

import com.damdamdeo.email_notifier.domain.EmailNotifier;
import com.damdamdeo.email_notifier.domain.TemplateGenerator;

import java.util.Objects;

public final class NotifyTodoCreatedUseCase implements UseCase<NotifyTodoCreatedCommand, Void> {

    private final TemplateGenerator templateGenerator;
    private final EmailNotifier emailNotifier;

    public NotifyTodoCreatedUseCase(final TemplateGenerator templateGenerator, final EmailNotifier emailNotifier) {
        this.templateGenerator = Objects.requireNonNull(templateGenerator);
        this.emailNotifier = Objects.requireNonNull(emailNotifier);
    }

    @Override
    public Void execute(final NotifyTodoCreatedCommand command) throws UseCaseException {
        final String content = templateGenerator.generateTodoCreated(command.todoDomain());
        emailNotifier.notify("New Todo created", content);
        return null;
    }

}
