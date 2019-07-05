package com.damdamdeo.eventsourcing.infrastructure;

import com.damdamdeo.eventsourcing.domain.Event;
import com.damdamdeo.eventsourcing.domain.Payload;
import com.damdamdeo.eventsourcing.infrastructure.hibernate.user.types.JSONBMetadataUserType;
import com.damdamdeo.eventsourcing.infrastructure.hibernate.user.types.JSONBPayloadUserType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@TypeDef(name = "jsonbMetaData", typeClass = JSONBMetadataUserType.class)
@TypeDef(name = "jsonbPayload", typeClass = JSONBPayloadUserType.class)
@Table(name = "Event")
@Entity
@NamedQuery(name = "Events.findByAggregateRootIdOrderByVersionAsc",
        query = "SELECT e FROM EventEntity e WHERE e.aggregateRootId = :aggregateRootId and e.aggregateRootType = :aggregateRootType ORDER BY e.version ASC")
public class EventEntity {

    @Id
    @Type(type = "pg-uuid")
    private UUID eventId;

    @NotNull
    private String aggregateRootId;

    @NotNull
    private String aggregateRootType;

    @NotNull
    private String eventType;

    @NotNull
    private Long version;

    @NotNull
    private Date creationDate;

    @NotNull
    @Type(type = "jsonbMetaData")
    private Map<String, Object> metaData;

    @NotNull
    @Type(type = "jsonbPayload")
    private Payload payload;

    public EventEntity() {};

    public EventEntity(final Event event) {
        this.eventId = event.eventId();
        this.aggregateRootId = event.aggregateRootId();
        this.aggregateRootType = event.aggregateRootType();
        this.eventType = event.eventType();
        this.version = event.version();
        this.creationDate = event.creationDate();
        this.metaData = event.metaData();
        this.payload = event.payload();
    }

    public Event toEvent() {
        return new Event(eventId,
                aggregateRootId,
                aggregateRootType,
                eventType,
                version,
                creationDate,
                payload,
                metaData);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventEntity that = (EventEntity) o;
        return Objects.equals(eventId, that.eventId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId);
    }

}
