## export realms with user and password
> users are not available from console following security
> https://github.com/keycloak/keycloak-documentation/blob/master/server_admin/topics/export-import.adoc

> from container `docker exec -it eventstore-quarkus-sample-app_keycloak_1 bash`

/opt/jboss/keycloak/bin/standalone.sh -Djboss.socket.binding.port-offset=100 \
  -Dkeycloak.migration.action=export \
  -Dkeycloak.migration.provider=dir \
  -Dkeycloak.migration.dir=/tmp/export \
  -Dkeycloak.migration.usersExportStrategy=REALM_FILE

> after the server startup just kill it
> files are presents here /tmp/export
> we just want todos realm with users
> copy file from container to host `docker cp eventstore-quarkus-sample-app_keycloak_1:/tmp/export/todos-realm.json /tmp/todos-realm.json`
