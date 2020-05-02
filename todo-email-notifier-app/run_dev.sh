#!/bin/bash
$(dirname $0)/../scripts/flush_vault.sh
mvn clean compile quarkus:dev
