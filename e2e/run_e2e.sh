#!/bin/bash
oc login 192.168.56.101:8443  -u sandbox -p sandbox; \
mvn clean verify
