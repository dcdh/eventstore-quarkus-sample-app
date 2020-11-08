package com.damdamdeo.todo.resources;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Collections;
import java.util.Map;

public class KafkaQuarkusTestResourceLifecycleManager implements QuarkusTestResourceLifecycleManager {

    private final Logger logger = LoggerFactory.getLogger(KafkaQuarkusTestResourceLifecycleManager.class);

    private final static String DEBEZIUM_VERSION = "1.3.0.Final";
    private final static Integer KAFKA_PORT = 9092;

    private GenericContainer<?> zookeeperContainer;

    private GenericContainer<?> kafkaContainer;

    private Network network = Network.newNetwork();

    @Override
    public Map<String, String> start() {
        final Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
        zookeeperContainer = new GenericContainer<>("debezium/zookeeper:" + DEBEZIUM_VERSION)
                .withNetwork(network)
                .withNetworkAliases("zookeeper")
                .waitingFor(Wait.forLogMessage(".*Started.*", 1));
        zookeeperContainer.start();

        kafkaContainer = new GenericContainer<>("debezium/kafka:" + DEBEZIUM_VERSION)
                .withNetwork(network)
                .withExposedPorts(KAFKA_PORT)
                .withEnv("ZOOKEEPER_CONNECT", "zookeeper:2181")
                .withEnv("CREATE_TOPICS", "event:3:1:compact") // 3 partitions 1 replica
                .waitingFor(Wait.forLogMessage(".*started.*", 1));
        kafkaContainer.start();
//        kafkaContainer.followOutput(logConsumer);
        System.setProperty("mp.messaging.incoming.event-in.bootstrap.servers",
                String.format("%s:%s", "localhost", kafkaContainer.getMappedPort(KAFKA_PORT)));
        return Collections.emptyMap();
    }

    @Override
    public void stop() {
        System.clearProperty("mp.messaging.incoming.event-in.bootstrap.servers");
        if (zookeeperContainer != null) {
            zookeeperContainer.close();
        }
        if (kafkaContainer != null) {
            kafkaContainer.close();
        }
        if (network != null) {
            network.close();
        }
    }

}
