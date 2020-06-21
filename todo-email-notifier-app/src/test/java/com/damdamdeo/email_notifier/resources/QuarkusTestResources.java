package com.damdamdeo.email_notifier.resources;

import io.quarkus.test.common.QuarkusTestResource;

@QuarkusTestResource(KafkaQuarkusTestResourceLifecycleManager.class)
@QuarkusTestResource(MailhogQuarkusTestResourceLifecycleManager.class)
@QuarkusTestResource(PostgreSQLQuarkusTestResourceLifecycleManager.class)
public class QuarkusTestResources {
}
