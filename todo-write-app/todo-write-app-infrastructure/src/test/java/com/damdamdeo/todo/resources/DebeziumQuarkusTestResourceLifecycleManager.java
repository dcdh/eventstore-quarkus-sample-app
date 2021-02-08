package com.damdamdeo.todo.resources;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;

import java.util.Collections;
import java.util.Map;

public class DebeziumQuarkusTestResourceLifecycleManager implements QuarkusTestResourceLifecycleManager {

    private final Logger logger = LoggerFactory.getLogger(DebeziumQuarkusTestResourceLifecycleManager.class);

    private final static String DEBEZIUM_VERSION = "1.4.1.Final";
    private final static Integer KAFKA_PORT = 9092;
    private final static Integer DEBEZIUM_CONNECT_API_PORT = 8083;

    private Network network;

    private PostgreSQLContainer<?> postgresMutableContainer;

    private GenericContainer<?> zookeeperContainer;

    private GenericContainer<?> kafkaContainer;

    private GenericContainer<?> debeziumConnectContainer;

    @Override
    public Map<String, String> start() {
        final Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
        network = Network.newNetwork();
        // I can't use my image 'dcdh1983/postgresql-10-debezium-centos7:latest' because environment variables and
        // cmd use to run container is hardcoded in PostgreSQLContainer and do not reflect my image
        // I could write one but I also do a big e2e test in OKD for a real application. I will write a specific one in my todo-app ;)
        final String networkAliases = "mutable";
        postgresMutableContainer = new PostgreSQLContainer<>(
                DockerImageName.parse("debezium/postgres:11-alpine").asCompatibleSubstituteFor("postgres"))
                .withDatabaseName("mutable")
                .withUsername("postgresuser")
                .withPassword("postgrespassword")
                .withNetwork(network)
                .withNetworkAliases(networkAliases);
        postgresMutableContainer.start();
//        postgresMutableContainer.followOutput(logConsumer);
        System.setProperty("quarkus.datasource.jdbc.url", postgresMutableContainer.getJdbcUrl());
        System.setProperty("quarkus.datasource.username", postgresMutableContainer.getUsername());
        System.setProperty("quarkus.datasource.password", postgresMutableContainer.getPassword());
        System.setProperty("quarkus.datasource.secret-store.jdbc.url", postgresMutableContainer.getJdbcUrl());
        System.setProperty("quarkus.datasource.secret-store.username", postgresMutableContainer.getUsername());
        System.setProperty("quarkus.datasource.secret-store.password", postgresMutableContainer.getPassword());
        System.setProperty("quarkus.datasource.mutable.jdbc.url", postgresMutableContainer.getJdbcUrl());
        System.setProperty("quarkus.datasource.mutable.username", postgresMutableContainer.getUsername());
        System.setProperty("quarkus.datasource.mutable.password", postgresMutableContainer.getPassword());
        System.setProperty("quarkus.datasource.consumed-events.jdbc.url", postgresMutableContainer.getJdbcUrl());
        System.setProperty("quarkus.datasource.consumed-events.username", postgresMutableContainer.getUsername());
        System.setProperty("quarkus.datasource.consumed-events.password", postgresMutableContainer.getPassword());
        zookeeperContainer = new GenericContainer<>("debezium/zookeeper:" + DEBEZIUM_VERSION)
                .withNetwork(network)
                .withNetworkAliases("zookeeper")
                .waitingFor(Wait.forLogMessage(".*Started.*", 1));
        zookeeperContainer.start();
        kafkaContainer = new GenericContainer<>("debezium/kafka:" + DEBEZIUM_VERSION)
                .withNetwork(network)
                .withNetworkAliases("kafka")
                .withExposedPorts(KAFKA_PORT)
                .withEnv("ZOOKEEPER_CONNECT", "zookeeper:2181")
                .withEnv("CREATE_TOPICS", "event:3:1:compact") // 3 partitions 1 replica
                .waitingFor(Wait.forLogMessage(".*started.*", 1));
        kafkaContainer.start();
//        kafkaContainer.followOutput(logConsumer);
        debeziumConnectContainer = new GenericContainer<>("damdamdeo/eventsourced-mutable-kafka-connect:1.4.1.Final")
                .withNetwork(network)
                .withExposedPorts(DEBEZIUM_CONNECT_API_PORT)
                .withEnv("BOOTSTRAP_SERVERS", "kafka:" + KAFKA_PORT)
                .withEnv("GROUP_ID", "1")
                .withEnv("CONFIG_STORAGE_TOPIC", "my_connect_configs")
                .withEnv("OFFSET_STORAGE_TOPIC", "my_connect_offsets")
                .withEnv("STATUS_STORAGE_TOPIC", "my_connect_statuses")
                .waitingFor(Wait.forLogMessage(".*Finished starting connectors and tasks.*", 1));
        debeziumConnectContainer.start();
//        debeziumConnectContainer.followOutput(logConsumer);
        System.setProperty("mp.messaging.incoming.event-in.bootstrap.servers",
                String.format("%s:%s", "localhost", kafkaContainer.getMappedPort(KAFKA_PORT)));
        System.setProperty("kafka-connector-api/mp-rest/url",
                String.format("http://%s:%d", "localhost", debeziumConnectContainer.getMappedPort(DEBEZIUM_CONNECT_API_PORT)));

        System.setProperty("connector.mutable.database.hostname", "mutable");
        System.setProperty("connector.mutable.database.username", postgresMutableContainer.getUsername());
        System.setProperty("connector.mutable.database.password", postgresMutableContainer.getPassword());
        System.setProperty("connector.mutable.database.port", "5432");
        System.setProperty("connector.mutable.database.dbname", postgresMutableContainer.getDatabaseName());
        System.setProperty("connector.mutable.nbOfPartitionsInEventTopic", "3");
        System.setProperty("slot.drop.on.stop", "true");
        return Collections.emptyMap();
    }

    @Override
    public void stop() {
        System.clearProperty("quarkus.datasource.jdbc.url");
        System.clearProperty("quarkus.datasource.username");
        System.clearProperty("quarkus.datasource.password");
        System.clearProperty("quarkus.datasource.secret-store.jdbc.url");
        System.clearProperty("quarkus.datasource.secret-store.username");
        System.clearProperty("quarkus.datasource.secret-store.password");
        System.clearProperty("quarkus.datasource.mutable.jdbc.url");
        System.clearProperty("quarkus.datasource.mutable.username");
        System.clearProperty("quarkus.datasource.mutable.password");
        System.clearProperty("quarkus.datasource.consumed-events.jdbc.url");
        System.clearProperty("quarkus.datasource.consumed-events.username");
        System.clearProperty("quarkus.datasource.consumed-events.password");
        System.clearProperty("kafka-connector-api/mp-rest/url");
        System.clearProperty("connector.mutable.database.hostname");
        System.clearProperty("connector.mutable.database.username");
        System.clearProperty("connector.mutable.database.password");
        System.clearProperty("connector.mutable.database.port");
        System.clearProperty("connector.mutable.database.dbname");
        System.clearProperty("connector.mutable.nbOfPartitionsInEventTopic");
        System.clearProperty("slot.drop.on.stop");
        if (postgresMutableContainer != null) {
            postgresMutableContainer.close();
        }
        System.clearProperty("mp.messaging.incoming.event-in.bootstrap.servers");
        if (zookeeperContainer != null) {
            zookeeperContainer.close();
        }
        if (kafkaContainer != null) {
            kafkaContainer.close();
        }
        if (debeziumConnectContainer != null) {
            debeziumConnectContainer.close();
        }
        if (network != null) {
            network.close();
        }
    }

}
