#!/bin/bash
./mvnw package -Pnative -Dnative-image.container-runtime=docker
