FROM jboss/keycloak:10.0.1

ENV KEYCLOAK_IMPORT /opt/jboss/config/todos-realm.json

COPY todos-realm.json /opt/jboss/config/todos-realm.json

# Add Prometheus metrics endpoint following https://github.com/aerogear/keycloak-metrics-spi
# todos-realm.json has been changed accordingly
# /!\ warning: it seems that I cannot import master realm ...
# Event configuration must be defined manually !!!
ADD https://github.com/aerogear/keycloak-metrics-spi/releases/download/2.0.1/keycloak-metrics-spi-2.0.1.jar /opt/jboss/keycloak/standalone/deployments/keycloak-metrics-spi-2.0.1.jar
USER root
RUN chmod +rx /opt/jboss/keycloak/standalone/deployments/keycloak-metrics-spi-2.0.1.jar