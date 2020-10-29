# What is it

This is a specific version of debezium zookeeper including Prometheus JMX exporter.

To use it you have to declare an environment variable `SERVER_JVMFLAGS` with this value `-javaagent:/jmx_prometheus_javaagent.jar=8080:/config.yml`.

## How to ?

### Build it

> docker build -f Dockerfile -t damdamdeo/debezium-zookeeper-prometheus-exporter:1.2.0.Final .

### Run it

> docker run -e SERVER_JVMFLAGS="-javaagent:/jmx_prometheus_javaagent.jar=8081:/config.yml" damdamdeo/debezium-zookeeper-prometheus-exporter:1.2.0.Final

### Docker compose declaration

```yaml
  zookeeper:
      image: debezium/zookeeper-prometheus-exporter:1.1.1.Final
      ports:
        - 2181:2181
        - 2888:2888
        - 3888:3888
        - 7074:8081
      environment:
        - LOG_LEVEL=WARN
        - SERVER_JVMFLAGS=-javaagent:/jmx_prometheus_javaagent.jar=8081:/config.yml
```