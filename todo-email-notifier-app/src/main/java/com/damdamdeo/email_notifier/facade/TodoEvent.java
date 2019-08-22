package com.damdamdeo.email_notifier.facade;

import com.damdamdeo.email_notifier.domain.EmailNotifier;
import com.damdamdeo.email_notifier.domain.TemplateGenerator;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.concurrent.CompletionStage;

public interface TodoEvent {

    CompletionStage<Void> handle(final String eventId,
                                 final Long version,
                                 final EntityManager em,
                                 final TemplateGenerator templateGenerator,
                                 final EmailNotifier emailNotifier) throws IOException;

}
