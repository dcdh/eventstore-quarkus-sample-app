# Infra

## remove all running docker containers
> docker kill $(docker ps -aq);docker rm $(docker ps -aq);docker volume prune -f;docker network prune -f;

## run todo-query-app
> docker-compose -f docker-compose-local-run.yaml up --detach zookeeper kafka connect todo-query secret-store
> 
> docker-compose -f docker-compose-local-run.yaml up --detach todo-query-app

You need to wait for services to be up (just a few seconds) before running todo-query-app

## run todo-write-app
> docker-compose -f docker-compose-local-run.yaml up --detach zookeeper kafka connect mutable secret-store
>
> docker-compose -f docker-compose-local-run.yaml up --detach todo-write-app

You need to wait for services to be up (just a few seconds) before running todo-write-app

## docker

docker pull debezium/zookeeper:1.3.0.Final \
    && docker pull docker.elastic.co/logstash/logstash-oss:6.8.2 \
    && docker pull mailhog/mailhog:v1.0.0 \
    && docker pull confluentinc/cp-kafka:5.2.1 \
    && docker pull jboss/keycloak:10.0.1 \
    && docker pull dcdh1983/postgresql-10-debezium-centos7:latest \
    && docker pull debezium/postgres:11-alpine \
    && docker pull hazelcast/hazelcast:4.0.3 \
    && docker pull postgres:11-alpine \
    && docker pull registry.access.redhat.com/ubi8/ubi-minimal:8.1 \
    && docker pull docker.elastic.co/kibana/kibana-oss:6.8.2 \
    && docker pull debezium/kafka:1.3.0.Final \
    && docker pull danielqsj/kafka-exporter:v1.2.0 \
    && docker pull wrouesnel/postgres_exporter:v0.8.0 \
    && docker pull jaegertracing/all-in-one:latest \
    && docker pull prom/prometheus:v2.21.0 \
    && docker pull grafana/grafana:7.2.1 \
    && docker pull quay.io/quarkus/ubi-quarkus-native-image:20.2.0-java11 \
    && docker pull docker.elastic.co/elasticsearch/elasticsearch-oss:6.8.2 \
    && docker pull justwatch/elasticsearch_exporter:1.1.0

## todo-query
> psql -U postgres
> \c todo-query

## Kafka

### test consuming event
> connect to `connect` container
>
> bin/kafka-console-consumer.sh --bootstrap-server kafka:9092 --topic event --from-beginning
>
> curl http://connect:8083/connectors/event-sourced-connector/status
>
> bin/kafka-topics.sh --bootstrap-server kafka:9092 --describe --topic event
> bin/kafka-consumer-groups.sh --bootstrap-server kafka:9092 --describe --all-groups
> bin/kafka-console-consumer.sh --bootstrap-server kafka:9092 --topic event --from-beginning --partition 0 --property print.key=true
> bin/kafka-console-consumer.sh --bootstrap-server kafka:9092 --topic event --from-beginning --partition 1 --property print.key=true
> bin/kafka-console-consumer.sh --bootstrap-server kafka:9092 --topic event --from-beginning --partition 2 --property print.key=true

### list topics
> connect to `connect` container
> 
> bin/kafka-topics.sh --list --bootstrap-server kafka:9092

## ELK

http://localhost:5601/
