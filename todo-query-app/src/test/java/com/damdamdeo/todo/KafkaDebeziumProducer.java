package com.damdamdeo.todo;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.kafka.clients.producer.KafkaProducer;

import io.quarkus.kafka.client.serialization.JsonbSerializer;

@ApplicationScoped
public class KafkaDebeziumProducer {

    private final KafkaProducer<JsonObject, JsonObject> producer;

    public KafkaDebeziumProducer(@ConfigProperty(name = "mp.messaging.incoming.event-in.bootstrap.servers") final String servers) {
        final Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, servers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, JsonbSerializer.class.getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonbSerializer.class.getName());
        config.put(ProducerConfig.ACKS_CONFIG, "1");
        producer = new KafkaProducer(config);
    }

    public void produce(final String testResourceFileName) throws Exception {
        final JsonObject jsonTestResource = stringToJsonb(loadContentFromResource(testResourceFileName));
        final JsonObject key = jsonTestResource.getJsonObject("key");
        final JsonObject value = jsonTestResource.getJsonObject("value");

        producer.send(new ProducerRecord<>("event", key, value)).get();
    }

    public static String loadContentFromResource(final String testResourceFileName) {
        final InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(testResourceFileName);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    public static JsonObject stringToJsonb(final String jsonString) throws Exception {
        JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();
        return object;
    }
}
