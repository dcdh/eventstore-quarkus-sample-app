package com.damdamdeo.todo.resources;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

import java.util.Collections;
import java.util.Map;

public class KeycloakTestResourceLifecycleManager implements QuarkusTestResourceLifecycleManager {

    private final Logger logger = LoggerFactory.getLogger(KeycloakTestResourceLifecycleManager.class);

    private PostgreSQLContainer<?> postgresKeycloakContainer;

    private GenericContainer keycloakContainer;

    private Network network;

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
                .waitingFor(
                        Wait.forLogMessage(".*Started authorizationRevisions.*\\n", 1)
                );
        keycloakContainer.start();
//        keycloakContainer.followOutput(logConsumer);

        System.setProperty("quarkus.oidc.auth-server-url",
                String.format("http://localhost:%d/auth/realms/todos", keycloakContainer.getMappedPort(8080)));

        System.setProperty("keycloak.admin.adminRealm", "master");
        System.setProperty("keycloak.admin.clientId", "admin-cli");
        System.setProperty("keycloak.admin.username", keycloakContainer.getEnvMap().get("KEYCLOAK_USER").toString());
        System.setProperty("keycloak.admin.password", keycloakContainer.getEnvMap().get("KEYCLOAK_PASSWORD").toString());
        return Collections.emptyMap();
    }

    @Override
    public void stop() {
        System.clearProperty("quarkus.oidc.auth-server-url");

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
    }

}
