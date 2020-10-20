#!/bin/bash
docker kill $(docker ps -aq); docker rm $(docker ps -aq); docker volume prune -f; docker network prune -f; \
  mvn -f ../pom.xml clean install -pl todo-domain-api && \
  mvn -f ../pom.xml clean test -pl todo-domain-api,todo-query-app; \
  docker network prune -f
