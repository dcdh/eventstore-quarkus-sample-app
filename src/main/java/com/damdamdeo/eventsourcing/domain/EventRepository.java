package com.damdamdeo.eventsourcing.domain;

import java.util.List;

public interface EventRepository {

    void save(List<Event> events);

    List<Event> load(String aggregateRootId, String aggregateRootType);

}
