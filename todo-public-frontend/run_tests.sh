#!/bin/bash
docker kill $(docker ps -aq); docker rm $(docker ps -aq); docker volume prune -f; docker network prune -f; \
  export TESTCONTAINERS_RYUK_DISABLED=true; \
  mvn -f ../pom.xml clean test -pl todo-public-frontend; \
  docker network prune -f
