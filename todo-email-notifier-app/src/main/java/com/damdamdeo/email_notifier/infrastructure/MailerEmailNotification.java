package com.damdamdeo.email_notifier.infrastructure;

import com.damdamdeo.email_notifier.domain.EmailNotifier;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MailerEmailNotification implements EmailNotifier {

    @ConfigProperty(name = "sendTo")
    String sendTo;

    @Inject
    Mailer mailer;

    @Override
    public void notify(final String content, final String subject) {
        mailer.send(Mail.withHtml(sendTo, subject, content));
    }

}
