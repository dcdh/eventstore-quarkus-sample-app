FROM debezium/zookeeper:1.1.1.Final

ADD https://repo1.maven.org/maven2/io/prometheus/jmx/jmx_prometheus_javaagent/0.14.0/jmx_prometheus_javaagent-0.14.0.jar /jmx_prometheus_javaagent.jar

COPY config.yml /config.yml
USER root
RUN chmod +rx /config.yml
RUN chmod +rx /jmx_prometheus_javaagent.jar
