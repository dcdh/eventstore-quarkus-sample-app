package com.damdamdeo.todo.resources;

import io.quarkus.test.common.QuarkusTestResource;

@QuarkusTestResource(DebeziumQuarkusTestResourceLifecycleManager.class)
@QuarkusTestResource(KeycloakTestResourceLifecycleManager.class)
@QuarkusTestResource(HazelcastTestResourceLifecycleManager.class)
public class QuarkusTestResources {
}
