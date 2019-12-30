package com.damdamdeo.todo_graph_visualiser;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class TodoGraphVisualiserTest extends CommonTest {

    @Inject
    KafkaDebeziumProducer kafkaDebeziumProducer;

    @Test
    public void should_visualise_expected_graph() {
        // Given

        // When
//        je cahrge les données dans kafka
//
//        // Then
//je fais une requete en base pour ...
//        je devrais utiliser awaitability pour attendre que la todo soit en version 1
//                je devrais checker les valeurs de la TODO
//                je devrais checker les valeurs des events (deux + valeurs !!!)
    }

}
