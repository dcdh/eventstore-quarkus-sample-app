#!/bin/bash
########################################################################################################################
# Build
### prepare build
docker kill $(docker ps -aq)
docker rm $(docker ps -aq)
docker volume prune -f
docker network prune -f
export TESTCONTAINERS_RYUK_DISABLED=true

### build todo-write-app
mvn -f pom.xml clean install -pl todo-domain-api,todo-write-app || { echo 'build todo-write-app failed' ; exit 1; }
docker build -f todo-write-app/src/main/docker/Dockerfile.jvm -t damdamdeo/todo-write-app:latest todo-write-app

### build todo-query-app
mvn -f pom.xml clean install -pl todo-domain-api,todo-query-app || { echo 'build todo-query-app failed' ; exit 1; }
docker build -f todo-query-app/src/main/docker/Dockerfile.jvm -t damdamdeo/todo-query-app:latest todo-query-app

### build todo-email-notifier-app
mvn -f pom.xml clean install -pl todo-domain-api,todo-email-notifier-app || { echo 'build todo-email-notifier-app failed' ; exit 1; }
docker build -f todo-email-notifier-app/src/main/docker/Dockerfile.jvm -t damdamdeo/todo-email-notifier-app:latest todo-email-notifier-app

### build custom version of keycloak having specific realm and users
docker build -f todo-keycloak/Dockerfile -t damdamdeo/todo-keycloak:latest todo-keycloak

### build todo-public-frontend-app
mvn -f pom.xml clean install -pl todo-public-frontend || { echo 'build failed' ; exit 1; }
docker build -f todo-public-frontend/src/main/docker/Dockerfile.jvm -t damdamdeo/todo-public-frontend-app:latest todo-public-frontend




########################################################################################################################
# Run
docker kill $(docker ps -aq)
docker rm $(docker ps -aq)
docker volume prune -f
docker network prune -f

### start infra
docker-compose -f docker-compose-local-run.yaml up --detach zookeeper kafka connect mutable todo-query todo-email-notifier secret-store mailhog keycloak-db
sleep 20

### start keycloak
docker-compose -f docker-compose-local-run.yaml up --detach keycloak
sleep 5

### start todo-write-app
docker-compose -f docker-compose-local-run.yaml up --detach todo-write-app
sleep 5

### start todo-query-app
docker-compose -f docker-compose-local-run.yaml up --detach todo-query-app

### start todo-email-notifier-app
docker-compose -f docker-compose-local-run.yaml up --detach todo-email-notifier-app

### start todo-public-frontend-app
docker-compose -f docker-compose-local-run.yaml up --detach todo-public-frontend-app

# http://0.0.0.0:8084/swagger-ui/
# http://0.0.0.0:8085/swagger-ui/
# public: http://localhost:8086/
