package com.damdamdeo.todo.infrastructure;

import com.damdamdeo.todo.aggregate.TodoAggregateRoot;
import com.damdamdeo.todo.aggregate.TodoAggregateRootRepository;
import com.damdamdeo.todo.command.CreateNewTodoCommand;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
public class EnrichedTodoAggregateRootRepositoryTest {

    @Inject
    @DataSource("secret-store")
    AgroalDataSource secretStoreDataSource;

    @Inject
    @DataSource("mutable")
    AgroalDataSource mutableDataSource;

    @Inject
    TodoAggregateRootRepository todoAggregateRootRepository;

    @BeforeEach
    public void setup() {
        try (final Connection con = secretStoreDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            stmt.executeUpdate("TRUNCATE TABLE SECRET_STORE");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (final Connection con = mutableDataSource.getConnection();
             final Statement stmt = con.createStatement()) {
            stmt.executeUpdate("TRUNCATE TABLE AGGREGATE_ROOT_MATERIALIZED_STATE");
            stmt.executeUpdate("TRUNCATE TABLE EVENT");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void should_is_todo_existent_return_false_when_the_todo_is_not_present() {
        // Given

        // When
        final boolean isTodoExistent = todoAggregateRootRepository.isTodoExistent("todoId");

        // Then
        assertFalse(isTodoExistent);
    }

    @Test
    public void should_is_todo_existent_return_true_when_the_todo_is_present() {
        // Given
        final TodoAggregateRoot todoAggregateRoot = new TodoAggregateRoot("todoId");
        todoAggregateRoot.handle(new CreateNewTodoCommand("description"), "todoId");
        todoAggregateRootRepository.save(todoAggregateRoot);

        // When
        final boolean isTodoExistent = todoAggregateRootRepository.isTodoExistent("todoId");

        // Then
        assertTrue(isTodoExistent);
    }

}
