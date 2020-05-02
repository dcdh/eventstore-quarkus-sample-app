#!/bin/bash
$(dirname $0)/../scripts/init_vault.sh
mvn clean test
