#!/bin/bash
pushd .

## start containers use for tests
docker kill $(docker ps -aq)
docker rm $(docker ps -aq)
docker-compose -f docker-compose-test.yaml up --detach zookeeper kafka connect eventstore todo-query todo-email-notifier secret-store mailhog

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

## build todo-public-frontend-app
# start query et write as todo-public-frontend-app need it !
docker-compose -f docker-compose-test.yaml up --detach todo-write-app todo-query-app
sleep 5

mvn clean install -pl todo-domain-api,todo-public-frontend
cd todo-public-frontend && docker build -f src/main/docker/Dockerfile.jvm -t damdamdeo/todo-public-frontend-app:latest .

popd
pushd .

## all images have been build - kill, remove and restart infrastructure
docker kill $(docker ps -aq)
docker rm $(docker ps -aq)
docker-compose -f docker-compose-local-run.yaml up --detach zookeeper kafka connect eventstore todo-query todo-email-notifier secret-store mailhog

## sleep 30 sec to be up
sleep 30

## start todo-write-app

docker-compose -f docker-compose-local-run.yaml up --detach todo-write-app
sleep 5

## start todo-query-app
docker-compose -f docker-compose-local-run.yaml up --detach todo-query-app

## start todo-email-notifier-app
docker-compose -f docker-compose-local-run.yaml up --detach todo-email-notifier-app

curl -i -X POST -H "Accept:application/json" -H  "Content-Type:application/json" http://localhost:8083/connectors/ -d @debezium_run_local.json

## start todo-public-frontend-app
docker-compose -f docker-compose-local-run.yaml up --detach todo-public-frontend-app

# curl http://localhost:8083/connectors
# curl -X DELETE http://localhost:8083/connectors/todo-connector
# http://0.0.0.0:8084/swagger-ui/
# http://0.0.0.0:8085/swagger-ui/
# bin/kafka-topics.sh --list --bootstrap-server kafka:9092
# bin/kafka-console-consumer.sh --bootstrap-server kafka:9092 --topic event --from-beginning
