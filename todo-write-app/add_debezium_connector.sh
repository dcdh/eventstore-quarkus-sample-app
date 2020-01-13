#!/bin/sh
read -r -d '' connector_setup<<CONNECTOR_SETUP
{
  "name": "todo-connector",
  "config": {
    "connector.class": "io.debezium.connector.postgresql.PostgresConnector",
    "tasks.max": "1",
    "plugin.name": "wal2json",
    "database.hostname": "eventstore.OPENSHIFT_NAMESPACE.svc",
    "database.port": "5432",
    "database.user": "EVENTSTORE_DATABASE_USER",
    "database.password": "EVENTSTORE_DATABASE_PASSWORD",
    "database.dbname" : "eventstore",
    "database.server.name": "eventstore",
    "schema.whitelist": "public",
    "transforms": "route",
    "transforms.route.type": "org.apache.kafka.connect.transforms.RegexRouter",
    "transforms.route.regex": "([^.]+)\\.([^.]+)\\.([^.]+)",
    "transforms.route.replacement": "$3",
    "key.converter": "org.apache.kafka.connect.json.JsonConverter",
    "key.converter.schemas.enable": "false",
    "value.converter": "org.apache.kafka.connect.json.JsonConverter",
    "value.converter.schemas.enable": "false",
    "include.schema.changes": "false"
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
while [ $? -ne 200 ]
do
  echo "unable to setup connector... try again..."
  sleep 1
  add_debezium_connector
done
exit 0