package com.damdamdeo.eventsourcing.infrastructure.hibernate;

import org.hibernate.dialect.PostgreSQL82Dialect;

import java.sql.Types;

public class JSONBPostgreSQLDialect extends PostgreSQL82Dialect {

    public JSONBPostgreSQLDialect() {
        registerColumnType(Types.JAVA_OBJECT, "jsonb");
    }

}
