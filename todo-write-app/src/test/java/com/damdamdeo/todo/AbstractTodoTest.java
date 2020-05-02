package com.damdamdeo.todo;

import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import org.junit.jupiter.api.BeforeEach;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class AbstractTodoTest {

    @Inject
    EntityManager entityManager;

    @Inject
    @DataSource("secret-store")
    AgroalDataSource secretStoreDataSource;

    @BeforeEach
    @Transactional
    public void setup() {
        try (final Connection con = secretStoreDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            stmt.executeUpdate("TRUNCATE TABLE SecretStore");
        } catch (SQLException e) {
            // Do not throw an exception as the table is not present because the @PostConstruct in AgroalDataSourceSecretStore
            // has not be called yet... bug ?!?
            // throw new RuntimeException(e);
        }

        entityManager.createQuery("DELETE FROM EncryptedEventEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM AggregateRootEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM EventConsumerConsumedEntity").executeUpdate();
        entityManager.createQuery("DELETE FROM EventConsumedEntity").executeUpdate();
    }

}
