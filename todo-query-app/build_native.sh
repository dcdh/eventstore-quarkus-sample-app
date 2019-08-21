#!/bin/bash
export GRAALVM_HOME=/home/damien/Documents/03_projets/04_tools/graalvm-ce-19.2.0
export PATH=/home/damien/Documents/03_projets/04_tools/graalvm-ce-19.2.0/bin:/usr/local/bin:/usr/local/sbin/:/usr/bin
export JAVA_HOME=/home/damien/Documents/03_projets/04_tools/graalvm-ce-19.2.0
./mvnw clean package -Pnative
