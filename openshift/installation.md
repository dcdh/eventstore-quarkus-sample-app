docker pull openshift/jenkins-slave-nodejs-centos7:v3.11 && \
    docker pull openshift/jenkins-agent-maven-35-centos7:v3.11 && \
    docker pull openshift/jenkins-slave-maven-centos7:v3.11 && \
    docker pull openshift/jenkins-agent-nodejs-8-centos7:v3.11 && \
    docker pull openshift/jenkins-slave-base-centos7:v3.11 && \
    docker pull openshift/jenkins-2-centos7:v3.11 && \
    docker pull debezium/zookeeper:0.10 && \
    docker pull debezium/kafka:0.10 && \
    docker pull confluentinc/cp-kafka-rest:5.2.2-1 && \
    docker pull landoop/kafka-topics-ui:0.9.4 && \
    docker pull debezium/connect:0.10 && \
    docker pull debezium/postgres:11-alpine && \
    docker pull postgres:11-alpine && \
    docker pull quay.io/quarkus/centos-quarkus-maven:19.2.0.1

## Debezium

docker pull strimzi/operator:0.12.1 && \
    docker pull strimzi/kafka:0.12.1-kafka-2.2.1 && \
    docker pull docker.io/openshift/origin-docker-builder:v3.11.0
    

git clone -b 0.12.1 https://github.com/strimzi/strimzi-kafka-operator
cd strimzi-kafka-operator
oc project staging

oc create -f install/cluster-operator ; oc create -f examples/templates/cluster-operator ; \
    oc adm policy add-cluster-role-to-user strimzi-cluster-operator-namespaced --serviceaccount strimzi-cluster-operator ; \
    oc adm policy add-cluster-role-to-user strimzi-entity-operator --serviceaccount strimzi-cluster-operator ; \
    oc adm policy add-cluster-role-to-user strimzi-topic-operator --serviceaccount strimzi-cluster-operator

oc new-app strimzi-persistent -p CLUSTER_NAME=broker

oc new-app strimzi-connect-s2i -p CLUSTER_NAME=debezium -p KAFKA_CONNECT_BOOTSTRAP_SERVERS=broker-kafka:9092

mkdir -p plugins && cd plugins && \
curl http://central.maven.org/maven2/io/debezium/debezium-connector-postgres/0.9.5.Final/debezium-connector-postgres-0.9.5.Final-plugin.tar.gz | tar xz; && \
oc start-build debezium-connect --from-dir=. --follow

## Postgres

oc new-app postgresql-persistent -p DATABASE_SERVICE_NAME=eventstore -p POSTGRESQL_USER=postgresuser -p POSTGRESQL_PASSWORD=postgrespassword -p POSTGRESQL_DATABASE=eventstore -p POSTGRESQL_VERSION=9.6 -l name=eventstore

oc new-app postgresql-persistent -p DATABASE_SERVICE_NAME=todo-query -p POSTGRESQL_USER=postgresuser -p POSTGRESQL_PASSWORD=postgrespassword -p POSTGRESQL_DATABASE=todo-query -p POSTGRESQL_VERSION=9.6 -l name=todo-query

oc new-app postgresql-persistent -p DATABASE_SERVICE_NAME=todo-email-notifier -p POSTGRESQL_USER=postgresuser -p POSTGRESQL_PASSWORD=postgrespassword -p POSTGRESQL_DATABASE=todo-email-notifier -p POSTGRESQL_VERSION=9.6 -l name=todo-email-notifier


/**
oc exec -i -c kafka broker-kafka-0 -- curl -X POST \
    -H "Accept:application/json" \
    -H "Content-Type:application/json" \
    http://debezium-connect-api:8083/connectors -d @- <<'EOF'

{
    "name": "todo-connector",
    "config": {
        "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
        "tasks.max": "1",
        "database.hostname": "eventstore.staging.svc",
        "database.port": "5432",
        "database.user": "postgresuser",
        "database.password": "postgrespassword",
        "database.dbname" : "eventstore",
        "database.server.name": "eventstore.staging.svc",
        "schema.whitelist": "public",
        "transforms": "route",
        "transforms.route.type": "org.apache.kafka.connect.transforms.RegexRouter",
        "transforms.route.regex": "([^.]+)\\.([^.]+)\\.([^.]+)",
        "transforms.route.replacement": "$3"
    }
}
EOF




## Pipeline

oc create -f openshift/todo-write-app-pipeline.yml -n ci

