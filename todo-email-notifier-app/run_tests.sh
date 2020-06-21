#!/bin/bash
export TESTCONTAINERS_RYUK_DISABLED=true; \
  mvn -f ../pom.xml clean install -pl todo-domain-api && \
  mvn -f ../pom.xml clean test -pl todo-email-notifier-app; \
  docker network prune -f
