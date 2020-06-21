package com.damdamdeo.email_notifier.resources;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.KafkaContainer;

import java.util.Collections;
import java.util.Map;

public class KafkaQuarkusTestResourceLifecycleManager implements QuarkusTestResourceLifecycleManager {

    private KafkaContainer kafkaContainer;

    @Override
    public Map<String, String> start() {
        // confluentinc/cp-kafka:5.2.1
        kafkaContainer = new KafkaContainer("5.2.1");
        kafkaContainer.start();
        System.setProperty("mp.messaging.incoming.event-in.bootstrap.servers", kafkaContainer.getBootstrapServers());
        return Collections.emptyMap();
    }

    @Override
    public void stop() {
        System.clearProperty("mp.messaging.incoming.event-in.bootstrap.servers");
        if (kafkaContainer != null) {
            kafkaContainer.close();
        }
    }

}
