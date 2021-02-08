package com.damdamdeo.todo.publicfrontend.resources;

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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InfrastructureQuarkusTestResourceLifecycleManager implements QuarkusTestResourceLifecycleManager {

    private final Logger logger = LoggerFactory.getLogger(InfrastructureQuarkusTestResourceLifecycleManager.class);

    private final static String DEBEZIUM_VERSION = "1.4.1.Final";
    private final static Integer KAFKA_PORT = 9092;
    private final static Integer DEBEZIUM_CONNECT_API_PORT = 8083;

    private Network network;

    private PostgreSQLContainer<?> postgresKeycloakContainer;

    private GenericContainer keycloakContainer;

    private GenericContainer mailhogContainer;

    private PostgreSQLContainer<?> postgresSecretStoreContainer;

    private PostgreSQLContainer<?> postgresQueryContainer;

    private OkdPostgreSQLContainer<?> postgresMutableContainer;

    private GenericContainer<?> zookeeperContainer;
    private GenericContainer<?> kafkaContainer;
    private GenericContainer<?> debeziumConnectContainer;

    private GenericContainer todoQueryAppContainer;

    private GenericContainer todoWriteAppContainer;

    private GenericContainer hazelcastContainer;

    @Override
    public Map<String, String> start() {
        final Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
        network = Network.newNetwork();

        postgresKeycloakContainer = new PostgreSQLContainer<>(
                DockerImageName.parse("debezium/postgres:11-alpine").asCompatibleSubstituteFor("postgres"))
                .withDatabaseName("keycloak")
                .withUsername("keycloak")
                .withPassword("keycloak")
                .withNetwork(network)
                .withNetworkAliases("keycloak-db");
        postgresKeycloakContainer.start();
//        postgresKeycloakContainer.followOutput(logConsumer);

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
                .waitingFor(
                        Wait.forLogMessage(".*Started authorizationRevisions.*\\n", 1)
                );
        keycloakContainer.start();
//        keycloakContainer.followOutput(logConsumer);

        mailhogContainer = new GenericContainer("mailhog/mailhog:v1.0.0")
                .withExposedPorts(1025, 8025)
                .withNetwork(network)
                .withNetworkAliases("mailhog")
                .waitingFor(
                        Wait.forLogMessage(".*Serving.*\\n", 1)
                );
        mailhogContainer.start();
//        mailhogContainer.followOutput(logConsumer);

        postgresSecretStoreContainer = new PostgreSQLContainer<>(
                DockerImageName.parse("postgres:11-alpine").asCompatibleSubstituteFor("postgres"))
                .withDatabaseName("secret-store")
                .withUsername("postgres")
                .withPassword("postgres")
                .withNetwork(network)
                .withNetworkAliases("secret-store");
        postgresSecretStoreContainer.start();
//        postgresSecretStoreContainer.followOutput(logConsumer);

        postgresQueryContainer = new PostgreSQLContainer<>(
                DockerImageName.parse("postgres:11-alpine").asCompatibleSubstituteFor("postgres"))
                .withDatabaseName("todo-query")
                .withUsername("postgres")
                .withPassword("postgres")
                .withNetwork(network)
                .withNetworkAliases("todo-query");
        postgresQueryContainer.start();
//        postgresQueryContainer.followOutput(logConsumer);

        postgresMutableContainer = new OkdPostgreSQLContainer<>()
                .withDatabaseName("mutable")
                .withUsername("postgresuser")
                .withPassword("postgrespassword")
                .withNetwork(network)
                .withNetworkAliases("mutable");
        postgresMutableContainer.start();
//        postgresMutableContainer.followOutput(logConsumer);
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
                .withNetworkAliases("debeziumConnect")
                .withExposedPorts(DEBEZIUM_CONNECT_API_PORT)
                .withEnv("BOOTSTRAP_SERVERS", "kafka:" + KAFKA_PORT)
                .withEnv("GROUP_ID", "1")
                .withEnv("CONFIG_STORAGE_TOPIC", "my_connect_configs")
                .withEnv("OFFSET_STORAGE_TOPIC", "my_connect_offsets")
                .withEnv("STATUS_STORAGE_TOPIC", "my_connect_statuses")
                .waitingFor(Wait.forLogMessage(".*Finished starting connectors and tasks.*", 1));
        debeziumConnectContainer.start();
//        debeziumConnectContainer.followOutput(logConsumer);

        todoQueryAppContainer = new GenericContainer("damdamdeo/todo-query-native-app:latest")
                .withExposedPorts(8080)
                // keep jvm and native declaration for compatibility
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
                .withEnv("quarkus.http.host", "0.0.0.0")
                .withEnv("mp.messaging.incoming.event-in.bootstrap.servers", "kafka:9092")
                .withEnv("quarkus.datasource.jdbc.url", "jdbc:postgresql://todo-query:5432/todo-query")
                .withEnv("quarkus.datasource.username", postgresQueryContainer.getUsername())
                .withEnv("quarkus.datasource.password", postgresQueryContainer.getPassword())
                .withEnv("quarkus.datasource.secret-store.jdbc.url", "jdbc:postgresql://secret-store:5432/secret-store")
                .withEnv("quarkus.datasource.secret-store.username", postgresSecretStoreContainer.getUsername())
                .withEnv("quarkus.datasource.secret-store.password", postgresSecretStoreContainer.getPassword())
                .withEnv("quarkus.datasource.consumed-events.jdbc.url", "jdbc:postgresql://todo-query:5432/todo-query")
                .withEnv("quarkus.datasource.consumed-events.username", postgresQueryContainer.getUsername())
                .withEnv("quarkus.datasource.consumed-events.password", postgresQueryContainer.getPassword())
                .withEnv("quarkus.oidc.auth-server-url", "http://keycloak:8080/auth/realms/todos")
                .withNetwork(network)
                .waitingFor(
                        Wait.forLogMessage(".*started in.*\\n", 1)
                );
        todoQueryAppContainer.start();
//        todoQueryAppContainer.followOutput(logConsumer);


        hazelcastContainer = new GenericContainer("hazelcast/hazelcast:4.0.3")
                .withNetwork(network)
                .withNetworkAliases("hazelcast")
                .waitingFor(
                        Wait.forLogMessage(".*is STARTED.*\\n", 1)
                );
        hazelcastContainer.start();
//        hazelcastContainer.followOutput(logConsumer);

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
                        "-Dkafka-connector-api/mp-rest/url=http://debeziumConnect:8083",
                        "-Dconnector.mutable.database.hostname=mutable",
                        "-Dconnector.mutable.database.username=" + postgresMutableContainer.getUsername(),
                        "-Dconnector.mutable.database.password=" + postgresMutableContainer.getPassword(),
                        "-Dconnector.mutable.database.port=5432",
                        "-Dconnector.mutable.database.dbname=mutable",
                        "-Dconnector.mutable.nbOfPartitionsInEventTopic=3",
                        "-Dslot.drop.on.stop=true",
                        "-Dquarkus.oidc.auth-server-url=http://keycloak:8080/auth/realms/todos",
                        "-Dquarkus.hazelcast-client.cluster-name=dev",
                        "-Dquarkus.hazelcast-client.cluster-members=hazelcast:5701"
                ).collect(Collectors.joining(" ")))
                .withNetwork(network)
                .waitingFor(
                        Wait.forLogMessage(".*started in.*\\n", 1)
                );
        todoWriteAppContainer.start();
//        todoWriteAppContainer.followOutput(logConsumer);

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
        System.setProperty("connector.port", debeziumConnectContainer.getMappedPort(8083).toString());

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
        if (hazelcastContainer != null) {
            hazelcastContainer.close();
        }
        if (zookeeperContainer != null) {
            zookeeperContainer.close();
        }
        if (kafkaContainer != null) {
            kafkaContainer.close();
        }
        if (debeziumConnectContainer != null) {
            debeziumConnectContainer.close();
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
