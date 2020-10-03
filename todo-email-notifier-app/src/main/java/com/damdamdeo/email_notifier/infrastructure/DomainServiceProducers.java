package com.damdamdeo.email_notifier.infrastructure;

import com.damdamdeo.email_notifier.domain.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

public class DomainServiceProducers {

    @Inject
    TemplateGenerator templateGenerator;

    @Inject
    EmailNotifier emailNotifier;

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
