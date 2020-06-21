package com.damdamdeo.todo.aggregate;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.serialization.AggregateRootEventMetadata;

public final class DefaultEventMetadata extends AggregateRootEventMetadata {

    @Override
    public int hashCode() {
        return 1;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return true;
    }

}
