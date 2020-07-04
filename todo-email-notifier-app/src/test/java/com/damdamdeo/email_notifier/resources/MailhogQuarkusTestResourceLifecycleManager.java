package com.damdamdeo.email_notifier.resources;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.util.Collections;
import java.util.Map;

public class MailhogQuarkusTestResourceLifecycleManager implements QuarkusTestResourceLifecycleManager {

    private final Logger logger = LoggerFactory.getLogger(MailhogQuarkusTestResourceLifecycleManager.class);

    private GenericContainer mailhogGenericContainer;

    @Override
    public Map<String, String> start() {
        final Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
        mailhogGenericContainer = new GenericContainer("mailhog/mailhog:v1.0.0")
                .withExposedPorts(new Integer[] {1025, 8025});
        mailhogGenericContainer.start();
        mailhogGenericContainer.followOutput(logConsumer);
        System.setProperty("quarkus.mailer.port", mailhogGenericContainer.getMappedPort(1025).toString());
        System.setProperty("quarkus.mailer.api.port", mailhogGenericContainer.getMappedPort(8025).toString());
        return Collections.emptyMap();
    }

    @Override
    public void stop() {
        System.clearProperty("quarkus.mailer.port");
        System.clearProperty("quarkus.mailer.api.port");
        if (mailhogGenericContainer != null) {
            mailhogGenericContainer.close();
        }
    }

}
