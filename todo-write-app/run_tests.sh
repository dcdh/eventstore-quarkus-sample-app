#!/bin/bash
export TESTCONTAINERS_RYUK_DISABLED=true; \
  mvn -f ../pom.xml clean install -pl todo-domain-api && \
  mvn -f ../pom.xml clean test -pl todo-domain-api,todo-write-app; \
  docker network prune -f
