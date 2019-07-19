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
    docker pull postgres:11-alpine


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

oc new-app strimzi-ephemeral -p CLUSTER_NAME=broker -p ZOOKEEPER_NODE_COUNT=1 -p KAFKA_NODE_COUNT=1 -p KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 -p KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1


oc new-app strimzi-connect-s2i -p CLUSTER_NAME=debezium -p KAFKA_CONNECT_BOOTSTRAP_SERVERS=broker-kafka-bootstrap:9092 -p KAFKA_CONNECT_CONFIG_STORAGE_REPLICATION_FACTOR=1 -p KAFKA_CONNECT_OFFSET_STORAGE_REPLICATION_FACTOR=1 -p KAFKA_CONNECT_STATUS_STORAGE_REPLICATION_FACTOR=1 -p KAFKA_CONNECT_VALUE_CONVERTER_SCHEMAS_ENABLE=false -p KAFKA_CONNECT_KEY_CONVERTER_SCHEMAS_ENABLE=false

mkdir -p plugins && cd plugins && \
curl http://central.maven.org/maven2/io/debezium/debezium-connector-postgres/0.9.5.Final/debezium-connector-postgres-0.9.5.Final-plugin.tar.gz | tar xz; && \
oc start-build debezium-connect --from-dir=. --follow

## Postgres

oc new-app --template=postgresql-persistent -p DATABASE_SERVICE_NAME=todo-query -p POSTGRESQL_USER=postgres -p POSTGRESQL_PASSWORD=postgres -p POSTGRESQL_DATABASE=todoquery -p POSTGRESQL_VERSION=10
oc new-app --template=postgresql-persistent -p DATABASE_SERVICE_NAME=todo-email-notifier -p POSTGRESQL_USER=postgres -p POSTGRESQL_PASSWORD=postgres -p POSTGRESQL_DATABASE=todoemailnotifier -p POSTGRESQL_VERSION=10

## Pipeline

oc create -f todo-write-app-pipeline.yml -n staging

