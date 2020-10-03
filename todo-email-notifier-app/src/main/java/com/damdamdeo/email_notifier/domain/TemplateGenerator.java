package com.damdamdeo.email_notifier.domain;

public interface TemplateGenerator {

    String generateTodoCreated(TodoDomain todoDomainCreated);

    String generateTodoMarkedAsCompleted(TodoDomain todoDomainMarkedAsCompleted);

}
