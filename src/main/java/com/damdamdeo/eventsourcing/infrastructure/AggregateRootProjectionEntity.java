package com.damdamdeo.eventsourcing.infrastructure;

import com.damdamdeo.eventsourcing.domain.AggregateRoot;
import com.damdamdeo.eventsourcing.domain.AggregateRootProjection;
import com.damdamdeo.eventsourcing.infrastructure.hibernate.user.types.JSONBAggregateRootProjectionUserType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@TypeDef(name = "jsonbAggregateRoot", typeClass = JSONBAggregateRootProjectionUserType.class)
@Table(name = "AggregateRootProjection")
@Entity
public class AggregateRootProjectionEntity {

    @EmbeddedId
    private AggregateRootProjectionId aggregateRootProjectionId;

    @NotNull
    @Type(type = "jsonbAggregateRoot")
    private AggregateRoot aggregateRoot;

    @NotNull
    private Long version;

    public AggregateRootProjectionEntity() {}

    public AggregateRootProjectionEntity(final AggregateRootProjection aggregateRootProjection) {
        this.aggregateRootProjectionId = new AggregateRootProjectionId(aggregateRootProjection.aggregateRootId(),
                aggregateRootProjection.aggregateRootType());
        this.aggregateRoot = aggregateRootProjection.aggregateRoot();
        this.version = aggregateRootProjection.version();
    }

    public AggregateRootProjection toAggregateRootProjection() {
        return new AggregateRootProjection(this.aggregateRoot);
    }

    public AggregateRootProjectionId aggregateRootProjectionId() {
        return aggregateRootProjectionId;
    }

    public AggregateRoot aggregateRoot() {
        return aggregateRoot;
    }

    public Long version() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AggregateRootProjectionEntity that = (AggregateRootProjectionEntity) o;
        return Objects.equals(aggregateRootProjectionId, that.aggregateRootProjectionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootProjectionId);
    }
}
