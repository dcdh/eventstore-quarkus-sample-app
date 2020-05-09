#!/bin/sh
read -r -d '' connector_setup<<CONNECTOR_SETUP
{
  "name": "todo-connector",
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "tasks.max": "1",
    "plugin.name": "wal2json",
    "database.hostname": "event-store.OPENSHIFT_NAMESPACE.svc",
    "database.port": "5432",
    "database.user": "EVENTSTORE_DATABASE_USER",
    "database.password": "EVENTSTORE_DATABASE_PASSWORD",
    "database.dbname" : "event-store",
    "database.server.name": "event-store",
    "schema.whitelist": "public",
    "transforms": "route",
    "transforms.route.type": "org.apache.kafka.connect.transforms.RegexRouter",
    "transforms.route.regex": "([^.]+)\\.([^.]+)\\.([^.]+)",
    "transforms.route.replacement": "$3",
    "key.converter": "org.apache.kafka.connect.json.JsonConverter",
    "key.converter.schemas.enable": "false",
    "value.converter": "org.apache.kafka.connect.json.JsonConverter",
    "value.converter.schemas.enable": "false",
    "include.schema.changes": "false",
    "tombstones.on.delete": "false"
  }
}
CONNECTOR_SETUP
connector_setup=${connector_setup//OPENSHIFT_NAMESPACE/$OPENSHIFT_NAMESPACE}
connector_setup=${connector_setup//EVENTSTORE_DATABASE_USER/$EVENTSTORE_DATABASE_USER}
connector_setup=${connector_setup//EVENTSTORE_DATABASE_PASSWORD/$EVENTSTORE_DATABASE_PASSWORD}
add_debezium_connector(){
  return `curl --fail -o /dev/null -s -0 -v -X POST \
    -w "%{http_code}" http://debezium-connect-api.$OPENSHIFT_NAMESPACE.svc:8083/connectors/ \
    -H 'Accept:application/json' \
    -H 'Content-Type:application/json' \
    -d "$connector_setup"`
}
set -x
add_debezium_connector
add_connector_status_code=$?
# 201 created: case when application is first started and todo-connector should not be present
# 409 already present: case when application is restarted and todo-connector has been previously created
while [ "$add_connector_status_code" -ne 201 ] && [ "$add_connector_status_code" -ne 409 ]
do
  echo "unable to add todo-connector... try again..."
  sleep 1
  add_debezium_connector
  add_connector_status_code=$?
done
exit 0