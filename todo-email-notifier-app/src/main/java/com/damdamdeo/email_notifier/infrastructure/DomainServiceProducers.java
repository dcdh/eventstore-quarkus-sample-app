package com.damdamdeo.email_notifier.infrastructure;

import com.damdamdeo.email_notifier.domain.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

public class DomainServiceProducers {

    private final TemplateGenerator templateGenerator;

    private final EmailNotifier emailNotifier;

    public DomainServiceProducers(final TemplateGenerator templateGenerator, final EmailNotifier emailNotifier) {
        this.templateGenerator = templateGenerator;
        this.emailNotifier = emailNotifier;
    }

    @Produces
    @ApplicationScoped
    public TodoCreatedNotifierService todoCreatedNotifierServiceProducer() {
        return new DomainTodoCreatedNotifierService(templateGenerator, emailNotifier);
    }

    @Produces
    @ApplicationScoped
    public TodoMarkedAsCompletedNotifierService todoMarkedAsCompletedNotifierServiceProducer() {
        return new DomainTodoMarkedAsCompletedNotifierService(templateGenerator, emailNotifier);
    }

}
