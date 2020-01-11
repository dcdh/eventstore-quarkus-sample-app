docker pull quay.io/openshift/origin-cli:v3.11 && \
    docker pull debezium/zookeeper:0.10 && \
    docker pull debezium/kafka:0.10 && \
    docker pull confluentinc/cp-kafka-rest:5.2.2-1 && \
    docker pull landoop/kafka-topics-ui:0.9.4 && \
    docker pull debezium/connect:0.10 && \
    docker pull debezium/postgres:11-alpine && \
    docker pull postgres:11-alpine && \
    docker pull fabric8/java-alpine-openjdk8-jre && \
    docker pull mailhog/mailhog:v1.0.0 && \
    docker pull centos:8 && \
    docker pull dcdh1983/postgresql-10-debezium-centos7:latest && \
    docker pull giantswarm/tiny-tools && \
    docker pull docker.io/openshift/jenkins-2-centos7:v3.11 && \
    docker pull maven:3.6.3-jdk-8-slim && \
    docker pull neo4j:3.5.14

image dcdh1983/postgresql-10-debezium-centos7 source:
- https://github.com/dcdh/postgresql-10-debezium-alpine.git

ensure image will be pushed from dcdh1983/postgresql-10-debezium-centos7:latest to openshift/postgresql:10-debezium-centos7-latest
oc tag --source=docker dcdh1983/postgresql-10-debezium-centos7:latest openshift/postgresql:10-debezium-centos7-latest --reference-policy=local

git clone https://github.com/dcdh/eventstore-quarkus-sample-app.git

## Production

oc project production

### strimzi (kafka)

https://debezium.io/documentation/reference/0.10/operations/openshift.html

docker pull strimzi/operator:0.14.0 && \
    docker pull strimzi/kafka:0.14.0-kafka-2.3.0 && \
    docker pull strimzi/operator:0.14.0 && \
    docker pull strimzi/kafka-bridge:0.14.0

export STRIMZI_VERSION=0.14.0
git clone -b $STRIMZI_VERSION https://github.com/strimzi/strimzi-kafka-operator
cd strimzi-kafka-operator

oc create -f install/cluster-operator ; oc create -f examples/templates/cluster-operator ; \
    oc adm policy add-cluster-role-to-user strimzi-cluster-operator-namespaced --serviceaccount strimzi-cluster-operator ; \
    oc adm policy add-cluster-role-to-user strimzi-entity-operator --serviceaccount strimzi-cluster-operator ; \
    oc adm policy add-cluster-role-to-user strimzi-topic-operator --serviceaccount strimzi-cluster-operator

oc process strimzi-persistent -p CLUSTER_NAME=broker -p ZOOKEEPER_NODE_COUNT=1 -p KAFKA_NODE_COUNT=1 -p KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 -p KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR=1 | oc apply -f -

### Debezium

oc process strimzi-connect-s2i -p CLUSTER_NAME=debezium -p KAFKA_CONNECT_BOOTSTRAP_SERVERS=broker-kafka-bootstrap:9092 -p KAFKA_CONNECT_CONFIG_STORAGE_REPLICATION_FACTOR=1 -p KAFKA_CONNECT_OFFSET_STORAGE_REPLICATION_FACTOR=1 -p KAFKA_CONNECT_STATUS_STORAGE_REPLICATION_FACTOR=1 -p KAFKA_CONNECT_VALUE_CONVERTER_SCHEMAS_ENABLE=false -p KAFKA_CONNECT_KEY_CONVERTER_SCHEMAS_ENABLE=false | oc apply -f -

export DEBEZIUM_VERSION=0.10.0.Final && \
mkdir -p plugins && cd plugins && \
for PLUGIN in {mongodb,mysql,postgres}; do \
    curl http://central.maven.org/maven2/io/debezium/debezium-connector-$PLUGIN/$DEBEZIUM_VERSION/debezium-connector-$PLUGIN-$DEBEZIUM_VERSION-plugin.tar.gz | tar xz; \
done && \
oc start-build debezium-connect --from-dir=. --follow && \
cd .. && rm -rf plugins

### todo-write-app

oc process -f postgresql-persistent-template.yml -p DATABASE_SERVICE_NAME=eventstore -p POSTGRESQL_USER=postgresuser -p POSTGRESQL_PASSWORD=postgrespassword -p POSTGRESQL_DATABASE=eventstore -l app=todo-write-app | oc create -f -
oc process -f openshift/todo-write-app-template.yml -l app=todo-write-app | oc create -f -

### todo-query-app

oc process -f postgresql-persistent-template.yml -p DATABASE_SERVICE_NAME=todo-query -p POSTGRESQL_USER=postgresuser -p POSTGRESQL_PASSWORD=postgrespassword -p POSTGRESQL_DATABASE=todo-query -l app=todo-query-app | oc create -f -
oc process -f openshift/todo-query-app-template.yml -l app=todo-query-app | oc create -f -

### todo-email-notifier-app

oc process -f postgresql-persistent-template.yml -p DATABASE_SERVICE_NAME=todo-email-notifier -p POSTGRESQL_USER=postgresuser -p POSTGRESQL_PASSWORD=postgrespassword -p POSTGRESQL_DATABASE=todo-email-notifier -l app=todo-email-notifier-app | oc create -f -

oc process -f openshift/mailhog-template.yml -l app=todo-email-notifier-app | oc create -f -
oc process -f openshift/todo-email-notifier-app-template.yml -l app=todo-email-notifier-app | oc create -f -

### todo-graph-visualiser-app

oc process -f neo4j-persistent-template.yml -l app=graph | oc create -f -
oc process -f openshift/todo-graph-visualiser-app-template.yml -l app=todo-graph-visualiser-app | oc create -f -

## e2e

oc project e2e

### do kafka installation

### do debezium installation

### do applications !
Flute je dois creer les templates pour postgrest !!!
TODO : changer le pipeline go production...

TODO faire un pipeline pour runner les tests e2e
dans mon pipeline je dois fluser mon Kafka via un "oc exec" !!! ainsi que flusher toutes mes bases !!!! truncate all databases !!!
dans mon pipeline je dois reseter l'environnement e2e
est ce que je peux me connecter à un container d'un pod pour executer une commande ? a mon avis je dois faire un oc exec !!!
https://docs.openshift.com/container-platform/3.11/dev_guide/executing_remote_commands.html

putain mega compliqué de purger une queue kafka ... à mon avis le plus simple est de jouer sur la retention des messages !!!
voir comment configurer Strimzi pour le définir !!!


## Pipeline CI

oc process -f openshift/jenkins/pipeline-infrastructure.yml | oc create -f - -n ci

oc process -f openshift/jenkins/todo-write-app-pipeline.yml | oc create -f - -n ci

oc process -f openshift/jenkins/todo-query-app-pipeline.yml | oc create -f - -n ci

oc process -f openshift/jenkins/todo-email-notifier-app-pipeline.yml | oc create -f - -n ci

oc process -f openshift/jenkins/todo-graph-visualiser-app-pipeline.yml | oc create -f - -n ci

> #oc policy add-role-to-user edit system:serviceaccount:ci:default -n production
> allow serviceaccount to tag image in production project

> #oc policy add-role-to-user edit system:serviceaccount:ci:default -n ci
> allow serviceaccount to tag image in ci project

oc process -f openshift/jenkins/todo-app-go-production-pipeline.yml | oc create -f - -n ci
