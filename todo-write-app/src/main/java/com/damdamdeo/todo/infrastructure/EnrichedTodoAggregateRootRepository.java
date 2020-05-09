package com.damdamdeo.todo.infrastructure;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.AggregateRootRepository;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.UnknownAggregateRootException;
import com.damdamdeo.todo.aggregate.TodoAggregateRoot;
import com.damdamdeo.todo.aggregate.TodoAggregateRootRepository;
import io.agroal.api.AgroalDataSource;
import io.quarkus.agroal.DataSource;

import javax.enterprise.context.ApplicationScoped;
import java.sql.*;
import java.util.Objects;

// Wrapped AggregateRootRepository and exposed another dedicated method

@ApplicationScoped
public class EnrichedTodoAggregateRootRepository implements TodoAggregateRootRepository {

    private final AgroalDataSource aggregateRootProjectionEventStoreDataSource;
    private final AggregateRootRepository aggregateRootRepository;

    public EnrichedTodoAggregateRootRepository(@DataSource("aggregate-root-projection-event-store") final AgroalDataSource aggregateRootProjectionEventStoreDataSource,
                                               final AggregateRootRepository aggregateRootRepository) {
        this.aggregateRootProjectionEventStoreDataSource = Objects.requireNonNull(aggregateRootProjectionEventStoreDataSource);
        this.aggregateRootRepository = Objects.requireNonNull(aggregateRootRepository);
    }

    @Override
    public boolean isTodoExistent(final String todoIdToCheck) {
        try (final Connection con = aggregateRootProjectionEventStoreDataSource.getConnection();
             // I could use the aggregaterootid column directly however I wanted to do a request using serializedaggregateroot jsonb feature.
             // In an other application the data can be only present in serializedaggregateroot like email for example.
             final PreparedStatement stmt = con.prepareStatement("SELECT EXISTS (SELECT * FROM AGGREGATE_ROOT_PROJECTION WHERE aggregateroottype = 'TodoAggregateRoot' AND serializedaggregateroot->>'aggregateRootId' = ?)")) {
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
