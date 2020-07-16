package com.damdamdeo.todo.publicfrontend.resources;

import io.debezium.testing.testcontainers.DebeziumContainer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InfrastructureQuarkusTestResourceLifecycleManager implements QuarkusTestResourceLifecycleManager {

    private final Logger logger = LoggerFactory.getLogger(InfrastructureQuarkusTestResourceLifecycleManager.class);

    private Network network;

    private PostgreSQLContainer<?> postgresKeycloakContainer;

    private GenericContainer keycloakContainer;

    private GenericContainer mailhogContainer;

    private PostgreSQLContainer<?> postgresSecretStoreContainer;

    private PostgreSQLContainer<?> postgresQueryContainer;

    private OkdPostgreSQLContainer<?> postgresMutableContainer;

    private KafkaContainer kafkaContainer;

    private DebeziumContainer debeziumContainer;

    private GenericContainer todoQueryAppContainer;

    private GenericContainer todoWriteAppContainer;

    @Override
    public Map<String, String> start() {
        final Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
        network = Network.newNetwork();

        postgresKeycloakContainer = new PostgreSQLContainer<>("debezium/postgres:11-alpine")
                .withDatabaseName("keycloak")
                .withUsername("keycloak")
                .withPassword("keycloak")
                .withNetwork(network)
                .withNetworkAliases("keycloak-db");
        postgresKeycloakContainer.start();
        postgresKeycloakContainer.followOutput(logConsumer);

        keycloakContainer = new GenericContainer("damdamdeo/todo-keycloak:latest")
                .withExposedPorts(8080)
                .withEnv("KEYCLOAK_USER", "keycloak")
                .withEnv("KEYCLOAK_PASSWORD", "keycloak")
                .withEnv("DB_VENDOR", "postgres")
                .withEnv("DB_ADDR", "keycloak-db:5432")
                .withEnv("DB_DATABASE", "keycloak")
                .withEnv("DB_USER", "keycloak")
                .withEnv("DB_PASSWORD", "keycloak")
                .withNetwork(network)
                .withNetworkAliases("keycloak")
                .dependsOn(postgresKeycloakContainer)
                .waitingFor(
                        Wait.forLogMessage(".*Started authorizationRevisions.*\\n", 1)
                );
        keycloakContainer.start();
        keycloakContainer.followOutput(logConsumer);

        mailhogContainer = new GenericContainer("mailhog/mailhog:v1.0.0")
                .withExposedPorts(1025, 8025)
                .withNetwork(network)
                .withNetworkAliases("mailhog")
                .waitingFor(
                        Wait.forLogMessage(".*Serving.*\\n", 1)
                );
        mailhogContainer.start();
        mailhogContainer.followOutput(logConsumer);

        postgresSecretStoreContainer = new PostgreSQLContainer<>("postgres:11-alpine")
                .withDatabaseName("secret-store")
                .withUsername("postgres")
                .withPassword("postgres")
                .withNetwork(network)
                .withNetworkAliases("secret-store");
        postgresSecretStoreContainer.start();
        postgresSecretStoreContainer.followOutput(logConsumer);

        postgresQueryContainer = new PostgreSQLContainer<>("postgres:11-alpine")
                .withDatabaseName("todo-query")
                .withUsername("postgres")
                .withPassword("postgres")
                .withNetwork(network)
                .withNetworkAliases("todo-query");
        postgresQueryContainer.start();
        postgresQueryContainer.followOutput(logConsumer);

        postgresMutableContainer = new OkdPostgreSQLContainer<>()
                .withDatabaseName("mutable")
                .withUsername("postgresuser")
                .withPassword("postgrespassword")
                .withNetwork(network)
                .withNetworkAliases("mutable");
        postgresMutableContainer.start();
        postgresMutableContainer.followOutput(logConsumer);

        kafkaContainer = new KafkaContainer("5.2.1")
                .withNetwork(network)
                .withNetworkAliases("kafka");
        kafkaContainer.start();
//        kafkaContainer.followOutput(logConsumer);

        debeziumContainer = new DebeziumContainer("debezium/connect:1.2.0.Final")
                .withNetwork(network)
                .withNetworkAliases("connect")
                .withKafka(kafkaContainer)
                .dependsOn(kafkaContainer);
        debeziumContainer.start();
//        debeziumContainer.followOutput(logConsumer);

        todoQueryAppContainer = new GenericContainer("damdamdeo/todo-query-app:latest")
                .withExposedPorts(8080)
                .withEnv("JAVA_OPTIONS", Stream.of("-Dquarkus.http.host=0.0.0.0",
                        "-Dmp.messaging.incoming.event-in.bootstrap.servers=kafka:9092",
                        "-Dquarkus.datasource.jdbc.url=jdbc:postgresql://todo-query:5432/todo-query",
                        "-Dquarkus.datasource.username=" + postgresQueryContainer.getUsername(),
                        "-Dquarkus.datasource.password=" + postgresQueryContainer.getPassword(),
                        "-Dquarkus.datasource.secret-store.jdbc.url=jdbc:postgresql://secret-store:5432/secret-store",
                        "-Dquarkus.datasource.secret-store.username=" + postgresSecretStoreContainer.getUsername(),
                        "-Dquarkus.datasource.secret-store.password=" + postgresSecretStoreContainer.getPassword(),
                        "-Dquarkus.datasource.consumed-events.jdbc.url=jdbc:postgresql://todo-query:5432/todo-query",
                        "-Dquarkus.datasource.consumed-events.username=" + postgresQueryContainer.getUsername(),
                        "-Dquarkus.datasource.consumed-events.password=" + postgresQueryContainer.getPassword(),
                        "-Dquarkus.oidc.auth-server-url=http://keycloak:8080/auth/realms/todos"
                ).collect(Collectors.joining(" ")))
                .withNetwork(network)
                .dependsOn(kafkaContainer, debeziumContainer, postgresSecretStoreContainer, postgresQueryContainer)
                .waitingFor(
                        Wait.forLogMessage(".*started in.*\\n", 1)
                );
        todoQueryAppContainer.start();
        todoQueryAppContainer.followOutput(logConsumer);

        todoWriteAppContainer = new GenericContainer("damdamdeo/todo-write-app:latest")
                .withExposedPorts(8080)
                .withEnv("JAVA_OPTIONS", Stream.of("-Dquarkus.http.host=0.0.0.0",
                        "-Dmp.messaging.incoming.event-in.bootstrap.servers=kafka:9092",
                        "-Dquarkus.datasource.jdbc.url=jdbc:postgresql://mutable:5432/mutable",
                        "-Dquarkus.datasource.username=" + postgresMutableContainer.getUsername(),
                        "-Dquarkus.datasource.password=" + postgresMutableContainer.getPassword(),
                        "-Dquarkus.datasource.secret-store.jdbc.url=jdbc:postgresql://secret-store:5432/secret-store",
                        "-Dquarkus.datasource.secret-store.username=" + postgresSecretStoreContainer.getUsername(),
                        "-Dquarkus.datasource.secret-store.password=" + postgresSecretStoreContainer.getPassword(),
                        "-Dquarkus.datasource.mutable.jdbc.url=jdbc:postgresql://mutable:5432/mutable",
                        "-Dquarkus.datasource.mutable.username=" + postgresMutableContainer.getUsername(),
                        "-Dquarkus.datasource.mutable.password=" + postgresMutableContainer.getPassword(),
                        "-Dquarkus.datasource.consumed-events.jdbc.url=jdbc:postgresql://mutable:5432/mutable",
                        "-Dquarkus.datasource.consumed-events.username=" + postgresMutableContainer.getUsername(),
                        "-Dquarkus.datasource.consumed-events.password=" + postgresMutableContainer.getPassword(),
                        "-Dkafka-connector-api/mp-rest/url=http://connect:8083",
                        "-Dconnector.mutable.database.hostname=mutable",
                        "-Dconnector.mutable.database.username=" + postgresMutableContainer.getUsername(),
                        "-Dconnector.mutable.database.password=" + postgresMutableContainer.getPassword(),
                        "-Dconnector.mutable.database.port=5432",
                        "-Dconnector.mutable.database.dbname=mutable",
                        "-Dslot.drop.on.stop=true",
                        "-Dquarkus.oidc.auth-server-url=http://keycloak:8080/auth/realms/todos"
                ).collect(Collectors.joining(" ")))
                .withNetwork(network)
                .dependsOn(kafkaContainer, debeziumContainer, postgresSecretStoreContainer, postgresMutableContainer)
                .waitingFor(
                        Wait.forLogMessage(".*started in.*\\n", 1)
                );
        todoWriteAppContainer.start();
        todoWriteAppContainer.followOutput(logConsumer);

        System.setProperty("quarkus.datasource.secret-store.db-kind", "postgresql");
        System.setProperty("quarkus.datasource.secret-store.jdbc.url", postgresSecretStoreContainer.getJdbcUrl());
        System.setProperty("quarkus.datasource.secret-store.username", postgresSecretStoreContainer.getUsername());
        System.setProperty("quarkus.datasource.secret-store.password", postgresSecretStoreContainer.getPassword());
        System.setProperty("quarkus.datasource.secret-store.jdbc.min-sipostgresqlze", "1");
        System.setProperty("quarkus.datasource.secret-store.jdbc.max-size", "11");

        System.setProperty("quarkus.datasource.todo-write.db-kind", "postgresql");
        System.setProperty("quarkus.datasource.todo-write.jdbc.url", postgresMutableContainer.getJdbcUrl());
        System.setProperty("quarkus.datasource.todo-write.username", postgresMutableContainer.getUsername());
        System.setProperty("quarkus.datasource.todo-write.password", postgresMutableContainer.getPassword());
        System.setProperty("quarkus.datasource.todo-write.jdbc.min-sipostgresqlze", "1");
        System.setProperty("quarkus.datasource.todo-write.jdbc.max-size", "11");

        System.setProperty("quarkus.datasource.todo-query.db-kind", "postgresql");
        System.setProperty("quarkus.datasource.todo-query.jdbc.url", postgresQueryContainer.getJdbcUrl());
        System.setProperty("quarkus.datasource.todo-query.username", postgresQueryContainer.getUsername());
        System.setProperty("quarkus.datasource.todo-query.password", postgresQueryContainer.getPassword());
        System.setProperty("quarkus.datasource.todo-query.jdbc.min-sipostgresqlze", "1");
        System.setProperty("quarkus.datasource.todo-query.jdbc.max-size", "11");

        System.setProperty("todo-write-api/mp-rest/url", "http://localhost:" + todoWriteAppContainer.getMappedPort(8080));
        System.setProperty("todo-query-api/mp-rest/url", "http://localhost:" + todoQueryAppContainer.getMappedPort(8080));
        System.setProperty("keycloak-api/mp-rest/url",
                String.format("http://localhost:%d/auth/realms/todos", keycloakContainer.getMappedPort(8080)));

        System.setProperty("quarkus.oidc.auth-server-url",
                String.format("http://localhost:%d/auth/realms/todos", keycloakContainer.getMappedPort(8080)));
        System.setProperty("connector.port", debeziumContainer.getMappedPort(8083).toString());

        System.setProperty("keycloak.admin.adminRealm", "master");
        System.setProperty("keycloak.admin.clientId", "admin-cli");
        System.setProperty("keycloak.admin.username", keycloakContainer.getEnvMap().get("KEYCLOAK_USER").toString());
        System.setProperty("keycloak.admin.password", keycloakContainer.getEnvMap().get("KEYCLOAK_PASSWORD").toString());
        return Collections.emptyMap();
    }

    @Override
    public void stop() {
        System.clearProperty("quarkus.datasource.secret-store.db-kind");
        System.clearProperty("quarkus.datasource.secret-store.jdbc.url");
        System.clearProperty("quarkus.datasource.secret-store.username");
        System.clearProperty("quarkus.datasource.secret-store.password");
        System.clearProperty("quarkus.datasource.secret-store.jdbc.min-sipostgresqlze");
        System.clearProperty("quarkus.datasource.secret-store.jdbc.max-size");

        System.clearProperty("quarkus.datasource.todo-write.db-kind");
        System.clearProperty("quarkus.datasource.todo-write.jdbc.url");
        System.clearProperty("quarkus.datasource.todo-write.username");
        System.clearProperty("quarkus.datasource.todo-write.password");
        System.clearProperty("quarkus.datasource.todo-write.jdbc.min-sipostgresqlze");
        System.clearProperty("quarkus.datasource.todo-write.jdbc.max-size");

        System.clearProperty("quarkus.datasource.todo-query.db-kind");
        System.clearProperty("quarkus.datasource.todo-query.jdbc.url");
        System.clearProperty("quarkus.datasource.todo-query.username");
        System.clearProperty("quarkus.datasource.todo-query.password");
        System.clearProperty("quarkus.datasource.todo-query.jdbc.min-sipostgresqlze");
        System.clearProperty("quarkus.datasource.todo-query.jdbc.max-size");

        System.clearProperty("todo-write-api/mp-rest/url");
        System.clearProperty("todo-query-api/mp-rest/url");

        System.clearProperty("quarkus.oidc.auth-server-url");
        System.clearProperty("connector.port");

        System.clearProperty("keycloak.admin.adminRealm");
        System.clearProperty("keycloak.admin.clientId");
        System.clearProperty("keycloak.admin.username");
        System.clearProperty("keycloak.admin.password");

        if (postgresKeycloakContainer != null) {
            postgresKeycloakContainer.close();
        }
        if (keycloakContainer != null) {
            keycloakContainer.close();
        }
        if (mailhogContainer != null) {
            mailhogContainer.close();
        }
        if (postgresSecretStoreContainer != null) {
            postgresSecretStoreContainer.close();
        }
        if (postgresQueryContainer != null) {
            postgresQueryContainer.close();
        }
        if (postgresMutableContainer != null) {
            postgresMutableContainer.close();
        }
        if (kafkaContainer != null) {
            kafkaContainer.close();
        }
        if (debeziumContainer != null) {
            debeziumContainer.close();
        }
        if (todoQueryAppContainer != null) {
            todoQueryAppContainer.close();
        }
        if (todoWriteAppContainer != null) {
            todoWriteAppContainer.close();
        }
        if (network != null) {
            network.close();
        }
    }

}
