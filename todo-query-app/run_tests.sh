#!/bin/bash
$(dirname $0)/../scripts/flush_vault.sh
mvn clean test
