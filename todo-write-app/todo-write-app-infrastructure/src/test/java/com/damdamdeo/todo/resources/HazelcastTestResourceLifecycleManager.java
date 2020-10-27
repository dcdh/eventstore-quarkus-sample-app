package com.damdamdeo.todo.resources;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Collections;
import java.util.Map;

public class HazelcastTestResourceLifecycleManager implements QuarkusTestResourceLifecycleManager {

    private final Logger logger = LoggerFactory.getLogger(HazelcastTestResourceLifecycleManager.class);

    private GenericContainer hazelcastContainer;

    @Override
    public Map<String, String> start() {
        final Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
        hazelcastContainer = new GenericContainer("hazelcast/hazelcast:4.0.3")
                .withExposedPorts(5701)
                .waitingFor(
                        Wait.forLogMessage(".*is STARTED.*\\n", 1)
                );
        hazelcastContainer.start();
        hazelcastContainer.followOutput(logConsumer);

        System.setProperty("HAZELCAST_IP", String.format("localhost:%d", hazelcastContainer.getMappedPort(5701)));
        return Collections.emptyMap();
    }

    @Override
    public void stop() {
        System.clearProperty("HAZELCAST_IP");

        if (hazelcastContainer != null) {
            hazelcastContainer.close();
        }
    }

    // docker run -p 5701:5701 hazelcast/hazelcast:4.0.3

}
