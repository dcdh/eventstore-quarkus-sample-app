package com.damdamdeo.email_notifier.resources;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.util.Collections;
import java.util.Map;

public class KafkaQuarkusTestResourceLifecycleManager implements QuarkusTestResourceLifecycleManager {

    private final Logger logger = LoggerFactory.getLogger(KafkaQuarkusTestResourceLifecycleManager.class);

    private KafkaContainer kafkaContainer;

    @Override
    public Map<String, String> start() {
        final Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
        // confluentinc/cp-kafka:5.2.1
        kafkaContainer = new KafkaContainer("5.2.1");
        kafkaContainer.start();
//        kafkaContainer.followOutput(logConsumer);
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
