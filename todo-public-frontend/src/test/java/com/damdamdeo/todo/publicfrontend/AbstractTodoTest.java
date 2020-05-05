package com.damdamdeo.todo.publicfrontend;

import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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

    @BeforeEach
    @Transactional
    public void setup() throws Exception {
        given()
                .when()
                .delete("http://localhost:8083/connectors/todo-connector");

        truncateTable(secretStoreDataSource, "secretstore");

        truncateTable(todoWriteDataSource, "aggregateroot");
        truncateTable(todoWriteDataSource, "event");
        truncateTable(todoWriteDataSource, "eventconsumed");
        truncateTable(todoWriteDataSource, "eventconsumerconsumed");
        truncateTable(todoWriteDataSource, "flyway_schema_history");

        truncateTable(todoQueryDataSource, "eventconsumed");
        truncateTable(todoQueryDataSource, "eventconsumerconsumed");
        truncateTable(todoQueryDataSource, "flyway_schema_history");
        truncateTable(todoQueryDataSource, "todoentity_aud");
        truncateTable(todoQueryDataSource, "revinfo");
        truncateTable(todoQueryDataSource, "todoentity");

        final ClassLoader classLoader = getClass().getClassLoader();
        try (final InputStream inputStream = classLoader.getResourceAsStream("debezium.json")) {
            final String debezium = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            given()
                    .contentType("application/json")
                    .accept("application/json")
                    .body(debezium)
                    .when()
                    .post("http://localhost:8083/connectors/")
                    .then()
                    .log()
                    .all()
                    .statusCode(201)
            ;
        }
        await().atMost(30, TimeUnit.SECONDS).until(() -> given()
                .get("http://localhost:8083/connectors/todo-connector/status")
                .then().log().all()
                .extract()
                .body().jsonPath().getList("tasks").isEmpty() == false);
        await().atMost(30, TimeUnit.SECONDS).until(() -> given()
                .get("http://localhost:8083/connectors/todo-connector/status")
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
