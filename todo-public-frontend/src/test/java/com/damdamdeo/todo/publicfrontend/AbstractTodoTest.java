package com.damdamdeo.todo.publicfrontend;

import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.BeforeEach;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static io.restassured.RestAssured.given;

public abstract class AbstractTodoTest {

    @Inject
    @DataSource("todo-write")
    AgroalDataSource todoWriteDataSource;

    @Inject
    @DataSource("todo-query")
    AgroalDataSource todoQueryDataSource;

    @Inject
    @DataSource("secret-store")
    AgroalDataSource secretStoreDataSource;

    @ConfigProperty(name = "connector.port")
    Integer connectorPort;

    @BeforeEach
    @Transactional
    public void setup() throws Exception {
        // https://docs.confluent.io/3.2.0/connect/managing.html
        given()
                .when()
                .put(String.format("http://localhost:%d/connectors/event-sourced-connector/pause", connectorPort))
                .then().log().all()
                .statusCode(202);

        truncateTable(secretStoreDataSource, "SECRET_STORE");

        truncateTable(todoWriteDataSource, "AGGREGATE_ROOT_MATERIALIZED_STATE");
        truncateTable(todoWriteDataSource, "EVENT");
        truncateTable(todoWriteDataSource, "CONSUMED_EVENT");
        truncateTable(todoWriteDataSource, "CONSUMED_EVENT_CONSUMER");

        truncateTable(todoQueryDataSource, "CONSUMED_EVENT");
        truncateTable(todoQueryDataSource, "CONSUMED_EVENT_CONSUMER");
        truncateTable(todoQueryDataSource, "flyway_schema_history");
        truncateTable(todoQueryDataSource, "todoentity_aud");
        truncateTable(todoQueryDataSource, "revinfo");
        truncateTable(todoQueryDataSource, "todoentity");

        given()
                .when()
                .put(String.format("http://localhost:%d/connectors/event-sourced-connector/resume", connectorPort))
                .then().log().all()
                .statusCode(202);

        await().atMost(30, TimeUnit.SECONDS).until(() -> given()
                .get(String.format("http://localhost:%d/connectors/event-sourced-connector/status", connectorPort))
                .then().log().all()
                .extract()
                .body().jsonPath().getList("tasks").isEmpty() == false);
        await().atMost(30, TimeUnit.SECONDS).until(() -> given()
                .get(String.format("http://localhost:%d/connectors/event-sourced-connector/status", connectorPort))
                .then().log().all()
                .extract()
                .body().jsonPath().getString("tasks[0].state").equals("RUNNING"));
        Thread.sleep(1000);
    }

    private void truncateTable(final AgroalDataSource agroalDataSource,
                               final String tableToTruncate) {
        try (final Connection con = agroalDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            stmt.executeUpdate(String.format("TRUNCATE TABLE %s CASCADE", tableToTruncate));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // psql -U postgres
    // \l
    // \c secret-store
    // \dt
}
