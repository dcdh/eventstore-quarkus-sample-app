#!/bin/bash
mvn clean install -pl todo-domain-api && \
  mvn clean test -pl todo-public-frontend
