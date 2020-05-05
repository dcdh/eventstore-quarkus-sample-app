#!/bin/bash
docker kill $(docker ps -aq); docker rm $(docker ps -aq); docker-compose -f todo-public-frontend/docker-compose.yaml up