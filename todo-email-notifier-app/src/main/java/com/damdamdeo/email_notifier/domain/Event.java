package com.damdamdeo.email_notifier.domain;

import java.util.Date;
import java.util.UUID;

public interface Event {

    UUID eventId();

    Date consumedAt();

}
