package com.damdamdeo.todo.infrastructure;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.AggregateRootRepository;
import com.damdamdeo.eventsourced.mutable.api.eventsourcing.UnknownAggregateRootException;
import com.damdamdeo.todo.domain.TodoAggregateRoot;
import com.damdamdeo.todo.domain.TodoAggregateRootRepository;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;

import javax.enterprise.context.ApplicationScoped;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

// Wrapped AggregateRootRepository and exposed another dedicated method

@ApplicationScoped
public class EnrichedTodoAggregateRootRepository implements TodoAggregateRootRepository {

    private final AgroalDataSource mutableDataSource;
    private final AggregateRootRepository aggregateRootRepository;

    public EnrichedTodoAggregateRootRepository(@DataSource("mutable") final AgroalDataSource mutableDataSource,
                                               final AggregateRootRepository aggregateRootRepository) {
        this.mutableDataSource = Objects.requireNonNull(mutableDataSource);
        this.aggregateRootRepository = Objects.requireNonNull(aggregateRootRepository);
    }

    @Override
    public boolean isTodoExistent(final String todoIdToCheck) {
        try (final Connection con = mutableDataSource.getConnection();
             // I could use the aggregaterootid column directly however I wanted to do a request using serializedaggregateroot jsonb feature.
             // In an other application the data can be only present in serializedaggregateroot like email for example.
             final PreparedStatement stmt = con.prepareStatement("SELECT EXISTS (SELECT * FROM AGGREGATE_ROOT_MATERIALIZED_STATE WHERE aggregateroottype = 'TodoAggregateRoot' AND serializedmaterializedstate->>'todoId' = ?)")) {
            stmt.setString(1, todoIdToCheck);
            try (final ResultSet resultSet = stmt.executeQuery()) {
                resultSet.next();
                return resultSet.getBoolean("exists");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TodoAggregateRoot save(final TodoAggregateRoot aggregateRoot) {
        return aggregateRootRepository.save(aggregateRoot);
    }

    @Override
    public TodoAggregateRoot load(final String aggregateRootId) throws UnknownAggregateRootException {
        return aggregateRootRepository.load(aggregateRootId, TodoAggregateRoot.class);
    }

}
