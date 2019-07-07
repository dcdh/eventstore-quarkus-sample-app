package com.damdamdeo.eventsourcing.infrastructure.hibernate.user.types;

import com.damdamdeo.eventsourcing.domain.AggregateRoot;
import com.damdamdeo.order.domain.AggregateRootAdapter;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;

public class JSONBAggregateRootProjectionUserType extends AbstractUserType {

    private static final Jsonb MAPPER = JsonbBuilder.create(new JsonbConfig()
            .withFormatting(true)
            .withAdapters(new AggregateRootAdapter()));

    @Override
    public Class returnedClass() {
        return AggregateRoot.class;
    }

    @Override
    protected Jsonb mapper() {
        return MAPPER;
    }

}