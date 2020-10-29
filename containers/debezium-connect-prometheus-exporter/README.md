# What is it

This is a specific version of debezium connect including Prometheus JMX exporter.

To use it you have to declare an environment variable `CONNECT_OPTS` with this value `-javaagent:/jmx_prometheus_javaagent.jar=8080:/config.yml`.

## How to ?

### Build it

> docker build -f Dockerfile -t damdamdeo/debezium-connect-prometheus-exporter:1.2.0.Final .

### Run it

> docker run -e EXTRA_ARGS="-javaagent:/jmx_prometheus_javaagent.jar=8080:/config.yml" -e BOOTSTRAP_SERVERS="localhost:9092" -e CONFIG_STORAGE_TOPIC="my_connect_configs" -e OFFSET_STORAGE_TOPIC="my_connect_offsets" -e STATUS_STORAGE_TOPIC="my_connect_statuses" damdamdeo/debezium-connect-prometheus-exporter:1.2.0.Final

### Docker compose declaration

```yaml
  connect:
    image: debezium/connect-prometheus-exporter:1.2.0.Final
    ports:
      - 8083:8083
    environment:
      - BOOTSTRAP_SERVERS=kafka:9092
      - GROUP_ID=1
      - CONFIG_STORAGE_TOPIC=my_connect_configs
      - OFFSET_STORAGE_TOPIC=my_connect_offsets
      - STATUS_STORAGE_TOPIC=my_connect_statuses
      - LOG_LEVEL=WARN
      - EXTRA_ARGS=-javaagent:/jmx_prometheus_javaagent.jar=8080:/config.yml
```