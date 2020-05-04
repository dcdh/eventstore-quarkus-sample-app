package com.damdamdeo.email_notifier.infrastructure;

import com.damdamdeo.email_notifier.domain.EmailNotifier;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.ReactiveMailer;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.Dependent;
import java.util.Objects;
import java.util.concurrent.CompletionStage;
import java.util.logging.Level;
import java.util.logging.Logger;

@Dependent
public class MailerEmailNotification implements EmailNotifier {

    private final static Logger LOGGER = Logger.getLogger(MailerEmailNotification.class.getName());

    final String sendTo;

    final ReactiveMailer mailer;

    public MailerEmailNotification(@ConfigProperty(name = "sendTo") final String sendTo,
                                   final ReactiveMailer mailer) {
        this.sendTo = Objects.requireNonNull(sendTo);
        this.mailer = Objects.requireNonNull(mailer);
    }

    @Override
    public CompletionStage<Void> notify(final String subject, final String content) {
        LOGGER.log(Level.INFO, "email notification with subject ''{0}'' and content ''{1}''", new Object[] {subject, content});
        return mailer.send(Mail.withHtml(sendTo, subject, content));
    }

}
