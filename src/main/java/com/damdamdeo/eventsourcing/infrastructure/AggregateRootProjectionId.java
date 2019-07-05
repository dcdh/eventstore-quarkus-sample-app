package com.damdamdeo.eventsourcing.infrastructure;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class AggregateRootProjectionId implements Serializable {

    @Column(name="aggregateRootId")
    private String aggregateRootId;

    @Column(name="aggregateRootType")
    private String aggregateRootType;

    public AggregateRootProjectionId() {}

    public AggregateRootProjectionId(final String aggregateRootId,
                                     final String aggregateRootType) {
        this.aggregateRootId = aggregateRootId;
        this.aggregateRootType = aggregateRootType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AggregateRootProjectionId that = (AggregateRootProjectionId) o;
        return Objects.equals(aggregateRootId, that.aggregateRootId) &&
                Objects.equals(aggregateRootType, that.aggregateRootType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aggregateRootId, aggregateRootType);
    }
}
