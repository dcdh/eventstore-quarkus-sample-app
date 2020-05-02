#!/bin/bash
docker kill $(docker ps -aq); docker rm $(docker ps -aq); docker-compose -f todo-write-app/docker-compose.yaml up
