package com.damdamdeo.email_notifier.infrastructure.email_notifier;

import com.damdamdeo.email_notifier.domain.EmailNotifier;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class MailerEmailNotification implements EmailNotifier {

    private final static Logger LOGGER = LoggerFactory.getLogger(MailerEmailNotification.class);

    final String sendTo;

    final Mailer mailer;

    public MailerEmailNotification(@ConfigProperty(name = "sendTo") final String sendTo,
                                   final Mailer mailer) {
        this.sendTo = Objects.requireNonNull(sendTo);
        this.mailer = Objects.requireNonNull(mailer);
    }

    @Override
    public void notify(final String subject, final String content) {
        LOGGER.info("email notification with subject ''{0}'' and content ''{1}''", new Object[] {subject, content});
        mailer.send(Mail.withHtml(sendTo, subject, content));
    }

}
