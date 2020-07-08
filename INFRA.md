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

### list topics
> connect to `connect` container
> 
> bin/kafka-topics.sh --list --bootstrap-server kafka:9092