#!/bin/bash
pushd .

## start containers use for tests
docker kill $(docker ps -aq)
docker rm $(docker ps -aq)
docker-compose -f docker-compose-test.yaml up --detach

sleep 30

## build todo-write-app
mvn clean install -pl todo-domain-api,todo-write-app
cd todo-write-app && docker build -f src/main/docker/Dockerfile.jvm -t damdamdeo/todo-write-app:latest .

popd
pushd .

## build todo-query-app
mvn clean install -pl todo-domain-api,todo-query-app
cd todo-query-app && docker build -f src/main/docker/Dockerfile.jvm -t damdamdeo/todo-query-app:latest .

popd
pushd .

## build todo-email-notifier-app
mvn clean install -pl todo-domain-api,todo-email-notifier-app
cd todo-email-notifier-app && docker build -f src/main/docker/Dockerfile.jvm -t damdamdeo/todo-email-notifier-app:latest .

popd
pushd .

## start infrastructure
docker kill $(docker ps -aq)
docker rm $(docker ps -aq)
docker-compose -f docker-compose-local-run.yaml up --detach zookeeper kafka connect eventstore todo-query todo-email-notifier secret_store mailhog

## sleep 30 sec to be up
sleep 30

## start todo-write-app

docker-compose -f docker-compose-local-run.yaml up --detach todo-write-app
sleep 5 sec up

## start todo-query-app
docker-compose -f docker-compose-local-run.yaml up --detach todo-query-app

## start todo-email-notifier-app
docker-compose -f docker-compose-local-run.yaml up --detach todo-email-notifier-app

read -r -d '' connector_setup<<CONNECTOR_SETUP
{
  "name": "todo-connector",
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "tasks.max": "1",
    "plugin.name": "wal2json",
    "database.hostname": "eventstore",
    "database.port": "5432",
    "database.user": "postgres",
    "database.password": "postgres",
    "database.dbname" : "eventstore",
    "database.server.name": "eventstore",
    "schema.whitelist": "public",
    "transforms": "route",
    "transforms.route.type": "org.apache.kafka.connect.transforms.RegexRouter",
    "transforms.route.regex": "([^.]+)\\\.([^.]+)\\\.([^.]+)",
    "transforms.route.replacement": "$3",
    "key.converter": "org.apache.kafka.connect.json.JsonConverter",
    "key.converter.schemas.enable": "false",
    "value.converter": "org.apache.kafka.connect.json.JsonConverter",
    "value.converter.schemas.enable": "false",
    "include.schema.changes": "false",
    "tombstones.on.delete": "false"
  }
}
CONNECTOR_SETUP
add_debezium_connector(){
  return `curl --fail -o /dev/null -s -0 -v -X POST \
    -w "%{http_code}" http://localhost:8083/connectors/ \
    -H 'Accept:application/json' \
    -H 'Content-Type:application/json' \
    -d "$connector_setup"`
}
add_debezium_connector

# curl http://localhost:8083/connectors
# curl -X DELETE http://localhost:8083/connectors/todo-connector
# http://0.0.0.0:8084/swagger-ui/
# http://0.0.0.0:8085/swagger-ui/
