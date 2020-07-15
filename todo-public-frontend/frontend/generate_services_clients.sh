#!/bin/bash

rm -rf src/generated && \
  mkdir -p src/generated && \
  api-spec-converter --from=openapi_3 --to=swagger_2 --syntax=json --order=alpha http://0.0.0.0:8086/openapi?format=json > /tmp/todo-public-frontend.json && \
  java -jar ../../tools/swagger-codegen-cli-2.4.13.jar generate -i /tmp/todo-public-frontend.json -l typescript-angular -o src/generated/ --additional-properties ngVersion=6.0.0 && \
  find src/generated/ -name *.ts | xargs sed -i 's#\(let formParams.*\); \};#\1 | HttpParams; \};#'
