package com.damdamdeo.email_notifier.domain;

import java.util.Date;
import java.util.UUID;

public interface EventStoreRepository {

    void markEventAsConsumed(UUID eventId, Date consumedAt);

    boolean hasConsumedEvent(UUID eventId);

}
