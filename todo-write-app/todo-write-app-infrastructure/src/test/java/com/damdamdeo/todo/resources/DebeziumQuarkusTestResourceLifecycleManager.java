package com.damdamdeo.todo.resources;

import io.debezium.testing.testcontainers.DebeziumContainer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import java.util.Collections;
import java.util.Map;

public class DebeziumQuarkusTestResourceLifecycleManager implements QuarkusTestResourceLifecycleManager {

    private final Logger logger = LoggerFactory.getLogger(DebeziumQuarkusTestResourceLifecycleManager.class);

    private Network network;

    private PostgreSQLContainer<?> postgresMutableContainer;

    private KafkaContainer kafkaContainer;

    private DebeziumContainer debeziumContainer;

    @Override
    public Map<String, String> start() {
        final Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
        network = Network.newNetwork();
        // I can't use my image 'dcdh1983/postgresql-10-debezium-centos7:latest' because environment variables and
        // cmd use to run container is hardcoded in PostgreSQLContainer and do not reflect my image
        // I could write one but I also do a big e2e test in OKD for a real application. I will write a specific one in my todo-app ;)
        final String networkAliases = "mutable";
        postgresMutableContainer = new PostgreSQLContainer<>("debezium/postgres:11")
                .withDatabaseName("mutable")
                .withUsername("postgresuser")
                .withPassword("postgrespassword")
                .withNetwork(network)
                .withNetworkAliases(networkAliases);
        postgresMutableContainer.start();
        postgresMutableContainer.followOutput(logConsumer);
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
        // confluentinc/cp-kafka:5.2.1
        kafkaContainer = new KafkaContainer("5.2.1")
                .withNetwork(network);
        kafkaContainer.start();
        kafkaContainer.followOutput(logConsumer);
        debeziumContainer = new DebeziumContainer("damdamdeo/eventsourced-mutable-kafka-connect:1.2.0.Final")
                .withNetwork(network)
                .withKafka(kafkaContainer)
                .dependsOn(kafkaContainer);
        debeziumContainer.start();
        debeziumContainer.followOutput(logConsumer);
        System.setProperty("mp.messaging.incoming.event-in.bootstrap.servers", kafkaContainer.getBootstrapServers());
        System.setProperty("kafka-connector-api/mp-rest/url",
                String.format("http://%s:%d", "localhost", debeziumContainer.getMappedPort(8083)));
        System.setProperty("connector.mutable.database.hostname", "mutable");
        System.setProperty("connector.mutable.database.username", postgresMutableContainer.getUsername());
        System.setProperty("connector.mutable.database.password", postgresMutableContainer.getPassword());
        System.setProperty("connector.mutable.database.port", "5432");
        System.setProperty("connector.mutable.database.dbname", postgresMutableContainer.getDatabaseName());
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
        System.clearProperty("slot.drop.on.stop");
        if (postgresMutableContainer != null) {
            postgresMutableContainer.close();
        }
        System.clearProperty("mp.messaging.incoming.event-in.bootstrap.servers");
        if (kafkaContainer != null) {
            kafkaContainer.close();
        }
        if (debeziumContainer != null) {
            debeziumContainer.close();
        }
        if (network != null) {
            network.close();
        }
    }

}
