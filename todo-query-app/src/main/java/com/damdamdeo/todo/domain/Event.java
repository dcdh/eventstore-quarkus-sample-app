package com.damdamdeo.todo.domain;

import java.util.Date;
import java.util.UUID;

public interface Event {

    UUID eventId();

    Date consumedAt();

}
