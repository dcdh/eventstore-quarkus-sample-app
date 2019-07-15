package com.damdamdeo.email_notifier.domain;

import java.util.concurrent.CompletionStage;

public interface EmailNotifier {

    CompletionStage<Void> notify(String subject, String content);

}
