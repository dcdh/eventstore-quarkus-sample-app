## Postgresql

> docker run --ulimit memlock=-1:-1 -it --rm=true --memory-swappiness=0 --name eventstore -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=eventstore -p 5432:5432 debezium/postgres:11-alpine

> docker exec -it eventstore /bin/bash

> pg_dump -d eventstore -U postgres

> psql -d eventstore -U postgres

> DROP SCHEMA public CASCADE;CREATE SCHEMA public;

> docker exec -it eventstorequarkussampleapp_eventstore_1 /bin/bash

## docker compose

> docker-compose up

> docker kill $(docker ps -aq) && docker rm $(docker ps -aq)

https://github.com/linkedin/cruise-control-ui/wiki

https://github.com/Landoop/kafka-topics-ui/issues/91

> docker rm $(docker ps -aq) && docker-compose up

> docker pull debezium/zookeeper:1.1.1.Final && \
  docker pull debezium/kafka:1.1.1.Final && \
  docker pull confluentinc/cp-kafka-rest:5.2.2-1 && \
  docker pull landoop/kafka-topics-ui:0.9.4 && \
  docker pull debezium/connect:1.1.1.Final && \
  docker pull debezium/postgres:11-alpine && \
  docker pull postgres:11-alpine && \
  docker pull jboss/keycloak:10.0.1


## debezium

https://github.com/debezium/debezium-examples/tree/master/tutorial

> il y a une conf à réaliser !

curl -i -X POST -H "Accept:application/json" -H  "Content-Type:application/json" http://localhost:8083/connectors/ -d '{"name": "todo-connector", "config": {"connector.class": "io.debezium.connector.postgresql.PostgresConnector", "tasks.max": "1", "database.hostname": "eventstore", "database.port": "5432", "database.user": "postgres", "database.password": "postgres", "database.dbname" : "eventstore", "database.server.name": "eventstore", "schema.whitelist": "public", "transforms": "route", "transforms.route.type": "org.apache.kafka.connect.transforms.RegexRouter", "transforms.route.regex": "([^.]+)\\.([^.]+)\\.([^.]+)", "transforms.route.replacement": "$3"}}'

curl -X DELETE http://localhost:8083/connectors/todo-connector

curl -X DELETE http://localhost:8082/topics/eventstore.public.aggregateroot
flute 405 !!! impossible de supprimer un topic depuis l'api rest ...

> donc je peux tester avec debezium uniquement en faisant du test e2e
