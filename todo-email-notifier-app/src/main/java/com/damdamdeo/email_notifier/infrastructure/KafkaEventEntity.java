package com.damdamdeo.email_notifier.infrastructure;

import com.damdamdeo.email_notifier.domain.Event;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

@Entity
public class KafkaEventEntity implements Event {

    @Id
    @Type(type = "pg-uuid")
    private UUID eventId;

    @NotNull
    private Date consumedAt;

    public KafkaEventEntity() {}

    public KafkaEventEntity(final UUID eventId,
                            final Date consumedAt) {
        this.eventId = eventId;
        this.consumedAt = consumedAt;
    }

    @Override
    public UUID eventId() {
        return eventId;
    }

    @Override
    public Date consumedAt() {
        return consumedAt;
    }

}
