package com.damdamdeo.email_notifier.resources;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.GenericContainer;

import java.util.Collections;
import java.util.Map;

public class MailhogQuarkusTestResourceLifecycleManager implements QuarkusTestResourceLifecycleManager {

    private GenericContainer mailhogGenericContainer;

    @Override
    public Map<String, String> start() {
        mailhogGenericContainer = new GenericContainer("mailhog/mailhog:v1.0.0")
                .withExposedPorts(new Integer[] {1025, 8025});
        mailhogGenericContainer.start();
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
