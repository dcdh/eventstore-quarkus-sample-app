package com.damdamdeo.eventsourcing.infrastructure.hibernate.user.types;

import com.damdamdeo.eventsourcing.domain.Payload;
import com.damdamdeo.order.domain.event.PayloadAdapter;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

public class JSONBPayloadUserType extends AbstractUserType {

    private static final Jsonb MAPPER = JsonbBuilder.create(new JsonbConfig()
            .withFormatting(true)
            .withAdapters(new PayloadAdapter()));

    @Override
    public Class returnedClass() {
        return Payload.class;
    }

    @Override
    protected Jsonb mapper() {
        return MAPPER;
    }

}