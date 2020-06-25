package com.damdamdeo.email_notifier.domain;

import java.io.IOException;

public interface TemplateGenerator {

    String generateTodoCreated(Todo todoCreated) throws IOException;

    String generateTodoMarkedAsCompleted(Todo todoMarkedAsCompleted) throws IOException;

}
