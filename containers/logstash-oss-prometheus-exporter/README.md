# What is it

This is a specific version of logstash including Prometheus JMX exporter.

To use it you have to declare an environment variable `LS_JAVA_OPTS` with this value `-javaagent:/jmx_prometheus_javaagent.jar=8080:/config.yml`.

## How to ?

### Build it

> docker build -f Dockerfile -t damdamdeo/logstash-oss-prometheus-exporter:6.8.2 .

### Run it

> docker run -e LS_JAVA_OPTS="-javaagent:/jmx_prometheus_javaagent.jar=8080:/config.yml" damdamdeo/logstash-oss-prometheus-exporter:6.8.2

### Docker compose declaration

```yaml
  logstash:
    image: damdamdeo/logstash-oss-prometheus-exporter:6.8.2
    volumes:
      - $HOME/pipelines:/usr/share/logstash/pipeline:z
    ports:
      - 12201:12201/udp
      - 5000:5000
      - 9600:9600
      - 7075:8080
    environment:
      - LS_JAVA_OPTS=-javaagent:/jmx_prometheus_javaagent.jar=8080:/config.yml
```