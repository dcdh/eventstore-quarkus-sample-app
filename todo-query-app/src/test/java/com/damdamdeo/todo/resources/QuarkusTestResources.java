package com.damdamdeo.todo.resources;

import io.quarkus.test.common.QuarkusTestResource;

@QuarkusTestResource(KafkaQuarkusTestResourceLifecycleManager.class)
@QuarkusTestResource(PostgreSQLQuarkusTestResourceLifecycleManager.class)
@QuarkusTestResource(KeycloakTestResourceLifecycleManager.class)
public class QuarkusTestResources {
}
