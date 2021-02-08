package com.damdamdeo.todo.publicfrontend.resources;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.wait.strategy.LogMessageWaitStrategy;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

import static java.time.temporal.ChronoUnit.SECONDS;

public class OkdPostgreSQLContainer<SELF extends OkdPostgreSQLContainer<SELF>> extends JdbcDatabaseContainer<SELF> {

    public static final Integer POSTGRESQL_PORT = 5432;
    private String databaseName = "test";
    private String username = "test";
    private String password = "test";

    public OkdPostgreSQLContainer() {
        super("dcdh1983/postgresql-10-debezium-centos7:latest");
        this.waitStrategy = new LogMessageWaitStrategy()
                .withRegEx(".*listening on IPv4 address.*\\s")
                .withStartupTimeout(Duration.of(60, SECONDS));
    }

    @Override
    public Set<Integer> getLivenessCheckPortNumbers() {
        return new HashSet<>(getMappedPort(POSTGRESQL_PORT));
    }

    @Override
    protected void configure() {
        addExposedPort(POSTGRESQL_PORT);
        addEnv("POSTGRESQL_DATABASE", databaseName);
        addEnv("POSTGRESQL_USER", username);
        addEnv("POSTGRESQL_PASSWORD", password);
    }

    @Override
    public String getDriverClassName() {
        return "org.postgresql.Driver";
    }

    @Override
    public String getJdbcUrl() {
        return "jdbc:postgresql://" + getContainerIpAddress() + ":" + getMappedPort(POSTGRESQL_PORT) + "/" + databaseName;
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    protected String getTestQueryString() {
        return "SELECT 1";
    }

    @Override
    public SELF withDatabaseName(final String databaseName) {
        this.databaseName = databaseName;
        return self();
    }

    @Override
    public SELF withUsername(final String username) {
        this.username = username;
        return self();
    }

    @Override
    public SELF withPassword(final String password) {
        this.password = password;
        return self();
    }

    @Override
    protected void waitUntilContainerStarted() {
        getWaitStrategy().waitUntilReady(this);
    }
}
