#!/bin/bash
./mvnw clean package -Pnative -Dnative-image.container-runtime=docker
