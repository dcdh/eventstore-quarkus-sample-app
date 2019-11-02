> force delete namespace !

https://blog.jefferyb.me/force-delete-openshift-project-namespace/

> force suppression service instance !

https://github.com/openshift/ansible-service-broker/issues/666

oc get serviceinstance -n staging -o yaml | sed "/kubernetes-incubator/d"| oc apply -f -

> debezium connector
curl http://localhost:8083/connectors
curl -X DELETE http://localhost:8083/connectors/todo-connector
