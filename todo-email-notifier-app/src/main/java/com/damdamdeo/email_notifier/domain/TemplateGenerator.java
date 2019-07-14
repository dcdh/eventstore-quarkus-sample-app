package com.damdamdeo.email_notifier.domain;

import java.io.IOException;

public interface TemplateGenerator {

    String generate(TodoCreated todoCreated) throws IOException;

    String generate(TodoMarkedAsCompleted todoMarkedAsCompleted) throws IOException;

}
