## Prerequists

Have nodejs install on your computer

### nodejs installation on centos7
> https://tecadmin.net/install-latest-nodejs-and-npm-on-centos/

```
yum install -y gcc-c++ make
curl -sL https://rpm.nodesource.com/setup_12.x | sudo -E bash -
sudo yum install nodejs -y

npm -v
6.14.4

node -v
v12.16.3
```

### install angular cli globally on the computer

sudo npm install @angular/cli -g

ng --version

### from `todo-public-frontend` create frontend app

ng new frontend

### add material design
> from frontend

```
ng add @angular/material
```

### generate services from endpoint

Start all applications `./build_and_run_all_app_local_app.sh`
> todo-public-frontend should be accessible here http://0.0.0.0:8086/swagger-ui/
> openapi.json `http://0.0.0.0:8086/openapi?format=json`

In project root path install swagger-codegen to generate ts service from swagger
> https://swagger.io/docs/open-source-tools/swagger-codegen/

```
mkdir tools
cd tools
wget https://oss.sonatype.org/content/repositories/releases/io/swagger/swagger-codegen-cli/2.4.13/swagger-codegen-cli-2.4.13.jar
```

Generate ts
> The sed is for bug : https://github.com/swagger-api/swagger-codegen/issues/8836
> Quarkus used OpenAPI version 3.0.1 which is not compatible with swagger-codegen-cli-2.4.13. It works with version 3.0.0-rc1 however an issue
> is present when generating typescript service (missing '}' at end of service).
> So I convert OpenAPI version 3.0.1 to swagger 2.0 to generate services using swagger-codegen-cli-2.4.13.

```
sudo npm install -g api-spec-converter
```

```
cd ../todo-public-frontend/ && \
  rm -rf frontend/src/generated && \
  mkdir -p frontend/src/generated && \
  api-spec-converter --from=openapi_3 --to=swagger_2 --syntax=json --order=alpha http://0.0.0.0:8086/openapi?format=json > /tmp/todo-public-frontend.json && \
  java -jar ../tools/swagger-codegen-cli-2.4.13.jar generate -i /tmp/todo-public-frontend.json -l typescript-angular -o frontend/src/generated/ --additional-properties ngVersion=6.0.0 && \
  find frontend/src/generated/ -name *.ts | xargs sed -i 's#\(let formParams.*\); \};#\1 | HttpParams; \};#'
```

