#!/bin/bash
mvn clean install -pl todo-domain-api && \
  mvn clean test -pl todo-domain-api,todo-write-app
