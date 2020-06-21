package com.damdamdeo.todo.resources;

import io.quarkus.test.common.QuarkusTestResource;

@QuarkusTestResource(KafkaQuarkusTestResourceLifecycleManager.class)
@QuarkusTestResource(PostgreSQLQuarkusTestResourceLifecycleManager.class)
public class QuarkusTestResources {
}
