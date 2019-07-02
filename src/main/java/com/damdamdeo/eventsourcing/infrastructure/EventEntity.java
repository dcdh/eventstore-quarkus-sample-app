package com.damdamdeo.eventsourcing.infrastructure;

import com.damdamdeo.eventsourcing.domain.Event;
import com.damdamdeo.eventsourcing.domain.Payload;
import com.damdamdeo.eventsourcing.infrastructure.hibernate.user.types.JSONBUserType;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@TypeDef(name = "jsonbMetaData", typeClass = JSONBUserType.class, parameters = {
        @Parameter(name = JSONBUserType.CLASS,
                value = "java.util.HashMap")})
@TypeDef(name = "jsonbPayload", typeClass = JSONBUserType.class, parameters = {
        @Parameter(name = JSONBUserType.CLASS,
                value = "com.damdamdeo.eventsourcing.domain.Payload")})
@Table(name = "Event")
@Entity
@NamedQuery(name = "Events.findByAggregateRootIdOrderByVersionAsc",
        query = "SELECT e FROM EventEntity e WHERE e.aggregateRootId = :aggregateRootId and e.aggregateRootType = :aggregateRootType ORDER BY e.version ASC")
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
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

}
