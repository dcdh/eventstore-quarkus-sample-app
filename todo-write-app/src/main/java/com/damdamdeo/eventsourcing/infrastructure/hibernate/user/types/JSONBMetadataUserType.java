package com.damdamdeo.eventsourcing.infrastructure.hibernate.user.types;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.util.Map;

public class JSONBMetadataUserType extends AbstractUserType {

    private static final Jsonb MAPPER = JsonbBuilder.create(new JsonbConfig()
            .withFormatting(true));

    @Override
    protected Jsonb mapper() {
        return MAPPER;
    }

    @Override
    public Class returnedClass() {
        return Map.class;
    }
}