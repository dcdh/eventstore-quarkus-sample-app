#!/bin/bash
# https://gist.github.com/ricardozanini/fa65e485251913e1467837b1c5a8ed28
########################################################################################################################
# Build
### prepare build
export GRAALVM_HOME=/usr/lib/jvm/graalvm/
export JAVA_HOME=${GRAALVM_HOME}
export PATH=${GRAALVM_HOME}/bin:$PATH

docker kill $(docker ps -aq)
docker rm $(docker ps -aq)
docker volume prune -f
docker network prune -f


docker build -f containers/debezium-connect-prometheus-exporter/Dockerfile -t damdamdeo/debezium-connect-prometheus-exporter:1.1.1.Final containers/debezium-connect-prometheus-exporter
docker build -f containers/debezium-zookeeper-prometheus-exporter/Dockerfile -t damdamdeo/debezium-zookeeper-prometheus-exporter:1.1.1.Final containers/debezium-zookeeper-prometheus-exporter
docker build -f containers/logstash-oss-prometheus-exporter/Dockerfile -t damdamdeo/logstash-oss-prometheus-exporter:6.8.2 containers/logstash-oss-prometheus-exporter

### build custom version of keycloak having specific realm and users
docker build -f todo-keycloak/Dockerfile -t damdamdeo/todo-keycloak:latest todo-keycloak

### build todo-write-app && todo-query-app (dependency with todo-domain-api)
# -amd or --also-make-dependents ensure that all modules using todo-domain-api will be build too.
mvn -f pom.xml clean install -pl todo-domain-api,todo-query-app -Pnative -Dquarkus.native.container-runtime=docker || { echo 'build todo-query-app in native mode failed' ; exit 1; }
docker build -f todo-query-app/src/main/docker/Dockerfile.native -t damdamdeo/todo-query-native-app:latest todo-query-app

mvn -f pom.xml clean install -pl 'todo-domain-api,todo-write-app,!todo-query-app' -amd || { echo 'build todo-write-app failed' ; exit 1; }
docker build -f todo-write-app/todo-write-app-infrastructure/src/main/docker/Dockerfile.jvm -t damdamdeo/todo-write-app:latest todo-write-app/todo-write-app-infrastructure

### build todo-email-notifier-native-app
mvn -f pom.xml clean install -pl todo-domain-api,todo-email-notifier-app -Pnative -Dquarkus.native.container-runtime=docker || { echo 'build todo-email-notifier-app in native mode failed' ; exit 1; }
docker build -f todo-email-notifier-app/src/main/docker/Dockerfile.native -t damdamdeo/todo-email-notifier-native-app:latest todo-email-notifier-app

### build todo-public-frontend-native-app
mvn -f pom.xml clean install package -pl todo-public-frontend -Pnative -Dquarkus.native.container-runtime=docker || { echo 'build todo-public-frontend in native mode failed' ; exit 1; }
docker build -f todo-public-frontend/src/main/docker/Dockerfile.native -t damdamdeo/todo-public-frontend-native-app:latest todo-public-frontend




########################################################################################################################
# Run
docker kill $(docker ps -aq)
docker rm $(docker ps -aq)
docker volume prune -f
docker network prune -f

### setup infra
mkdir -p $HOME/pipelines
cat << 'EOF' > $HOME/pipelines/gelf.conf
input {
  gelf {
    port => 12201
  }
}
output {
  stdout {}
  elasticsearch {
    hosts => ["http://elasticsearch:9200"]
  }
}
EOF

mkdir -p $HOME/prometheus
cat << 'EOF' > $HOME/prometheus/prometheus.yml
global:
  scrape_interval: 15s

  # Attach these labels to any time series or alerts when communicating with
  # external systems (federation, remote storage, Alertmanager).
  external_labels:
    env: 'local'

# la scrape configuration de prometheus, les hosts qu'il va contacter pour chercher les métrique
scrape_configs:
  - job_name: 'todo-public-frontend-app'
    metrics_path: /metrics
    scheme: http
    static_configs:
      - targets: ['todo-public-frontend-app:8080']
  - job_name: 'todo-email-notifier-app'
    metrics_path: /metrics
    scheme: http
    static_configs:
      - targets: ['todo-email-notifier-app:8080']
  - job_name: 'todo-query-app'
    metrics_path: /metrics
    scheme: http
    static_configs:
      - targets: ['todo-query-app:8080']
  - job_name: 'todo-write-app'
    metrics_path: /metrics
    scheme: http
    static_configs:
      - targets: ['todo-write-app:8080']
  - job_name: 'todo-email-notifier'
    metrics_path: /metrics
    scheme: http
    static_configs:
      - targets: ['todo-email-notifier-exporter:9187']
  - job_name: 'todo-query'
    metrics_path: /metrics
    scheme: http
    static_configs:
      - targets: ['todo-query-exporter:9187']
  - job_name: 'mutable'
    metrics_path: /metrics
    scheme: http
    static_configs:
      - targets: ['mutable-exporter:9187']
  - job_name: 'keycloak-db'
    metrics_path: /metrics
    scheme: http
    static_configs:
      - targets: ['keycloak-db-exporter:9187']
  - job_name: 'secret-store'
    metrics_path: /metrics
    scheme: http
    static_configs:
      - targets: ['secret-store-exporter:9187']
  - job_name: 'kafka'
    metrics_path: /metrics
    scheme: http
    static_configs:
      - targets: ['kafka-exporter:9308']
  - job_name: 'elasticsearch'
    metrics_path: /metrics
    scheme: http
    static_configs:
      - targets: ['elasticsearch-exporter:9114']
  - job_name: 'grafana'
    metrics_path: /metrics
    scheme: http
    static_configs:
      - targets: ['grafana:3000']
  - job_name: 'keycloak-master'
    metrics_path: /auth/realms/master/metrics
    scheme: http
    static_configs:
      - targets: ['keycloak:8080']
  - job_name: 'keycloak-todo'
    metrics_path: /auth/realms/todos/metrics
    scheme: http
    static_configs:
      - targets: ['keycloak:8080']
  - job_name: 'connect'
    metrics_path: /metrics
    scheme: http
    static_configs:
      - targets: ['connect:8080']
  - job_name: 'zookeeper'
    metrics_path: /metrics
    scheme: http
    static_configs:
      - targets: ['zookeeper:8081']
  - job_name: 'logstash'
    metrics_path: /metrics
    scheme: http
    static_configs:
      - targets: ['logstash:8080']
EOF

mkdir -p $HOME/grafana
cat << 'EOF' > $HOME/grafana/datasource.yml
apiVersion: 1

datasources:
  - name: Prometheus
    type: prometheus
    access: proxy
    url: http://prometheus:9090
EOF

cat << 'EOF' > $HOME/grafana/dashboards.yml
apiVersion: 1

providers:
  - name: 'Monitoring'
    orgId: 1
    folder: ''
    type: file
    disableDeletion: false
    updateIntervalSeconds: 10 #how often Grafana will scan for changed dashboards
    options:
      path: /var/lib/grafana/dashboards
EOF
### start infra
docker-compose -f docker-compose-local-native-run.yaml up --detach jaeger elasticsearch logstash kibana zookeeper kafka connect mutable todo-query todo-email-notifier secret-store mailhog keycloak-db hazelcast
sleep 20

### start keycloak
docker-compose -f docker-compose-local-native-run.yaml up --detach keycloak
sleep 20

### start todo-write-app
docker-compose -f docker-compose-local-native-run.yaml up --detach todo-write-app
sleep 5

### start todo-query-app
docker-compose -f docker-compose-local-native-run.yaml up --detach todo-query-app

### start todo-email-notifier-app
docker-compose -f docker-compose-local-native-run.yaml up --detach todo-email-notifier-app-1 todo-email-notifier-app-2

### start todo-public-frontend-app
docker-compose -f docker-compose-local-native-run.yaml up --detach todo-public-frontend-app

### monitoring
sleep 5
docker-compose -f docker-compose-local-native-run.yaml up --detach prometheus grafana mutable-exporter todo-query-exporter todo-email-notifier-exporter keycloak-db-exporter secret-store-exporter kafka-exporter elasticsearch-exporter

# http://0.0.0.0:8084/swagger-ui/
# http://0.0.0.0:8085/swagger-ui/
# public: http://localhost:8086/
