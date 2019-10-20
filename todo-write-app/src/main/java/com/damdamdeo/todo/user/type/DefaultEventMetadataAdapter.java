package com.damdamdeo.todo.user.type;

import com.damdamdeo.eventdataspreader.writeside.eventsourcing.api.EventMetadata;
import com.damdamdeo.eventdataspreader.writeside.eventsourcing.infrastructure.hibernate.user.types.EventMetadataAdapter;
import com.damdamdeo.todo.aggregate.event.DefaultEventMetadata;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.adapter.JsonbAdapter;

public class DefaultEventMetadataAdapter implements EventMetadataAdapter,
        JsonbAdapter<EventMetadata, JsonObject> // Quick fix an issue with yasson
    {

    @Override
    public JsonObject adaptToJson(final EventMetadata eventMetadata) {
        return Json.createObjectBuilder()
                .build();
    }

    @Override
    public EventMetadata adaptFromJson(final JsonObject eventMetadata) {
        return new DefaultEventMetadata();
    }

}
