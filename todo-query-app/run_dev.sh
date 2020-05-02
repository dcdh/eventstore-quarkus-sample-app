#!/bin/bash
$(dirname $0)/../scripts/init_vault.sh
mvn clean compile quarkus:dev
