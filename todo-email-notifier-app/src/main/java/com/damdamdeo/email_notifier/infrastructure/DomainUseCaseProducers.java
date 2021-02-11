package com.damdamdeo.email_notifier.infrastructure;

import com.damdamdeo.email_notifier.domain.EmailNotifier;
import com.damdamdeo.email_notifier.domain.TemplateGenerator;
import com.damdamdeo.email_notifier.domain.usecase.NotifyTodoCreatedUseCase;
import com.damdamdeo.email_notifier.domain.usecase.NotifyTodoMarkedAsCompletedUseCase;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.util.Objects;

public class DomainUseCaseProducers {

    private final TemplateGenerator templateGenerator;
    private final EmailNotifier emailNotifier;

    public DomainUseCaseProducers(final TemplateGenerator templateGenerator, final EmailNotifier emailNotifier) {
        this.templateGenerator = Objects.requireNonNull(templateGenerator);
        this.emailNotifier = Objects.requireNonNull(emailNotifier);
    }

    @Produces
    @ApplicationScoped
    public NotifyTodoCreatedUseCase produceNotifyTodoCreatedUseCase() {
        return new NotifyTodoCreatedUseCase(templateGenerator, emailNotifier);
    }

    @Produces
    @ApplicationScoped
    public NotifyTodoMarkedAsCompletedUseCase produceNotifyTodoMarkedAsCompletedUseCase() {
        return new NotifyTodoMarkedAsCompletedUseCase(templateGenerator, emailNotifier);
    }

}
