#!/bin/bash
docker kill $(docker ps -aq)
docker rm $(docker ps -aq)
docker-compose -f todo-email-notifier-app/docker-compose.yaml up --detach
# install pom base package
mvn clean install -Dmaven.test.skip=true
# run in debug mode using the port 5005
mvn clean compile quarkus:dev -Ddebug=true -pl todo-email-notifier-app
curl -i -X POST -H "Accept:application/json" -H  "Content-Type:application/json" http://localhost:8083/connectors/ -d @debezium_run_local.json
