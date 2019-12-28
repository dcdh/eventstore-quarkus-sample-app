#!/bin/bash
oc login 192.168.56.101:8443 -u sandbox -p sandbox && \
oc tag ci/todo-email-notifier-app:latest e2e/todo-email-notifier-app:latest
