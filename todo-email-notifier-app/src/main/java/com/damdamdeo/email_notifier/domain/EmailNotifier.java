package com.damdamdeo.email_notifier.domain;

public interface EmailNotifier {

    void notify(String subject, String content);

}
