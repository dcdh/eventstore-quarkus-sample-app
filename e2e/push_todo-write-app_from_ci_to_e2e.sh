#!/bin/bash
oc login 192.168.56.101:8443 -u sandbox -p sandbox && \
oc tag ci/todo-write-app:latest e2e/todo-write-app:latest
