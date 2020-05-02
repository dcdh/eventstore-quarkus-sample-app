#!/bin/sh
curl -X DELETE http://debezium-connect-api.$OPENSHIFT_NAMESPACE.svc:8083/connectors/todo-connector
