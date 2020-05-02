#!/bin/bash
docker kill $(docker ps -aq); docker rm $(docker ps -aq); docker-compose -f todo-query-app/docker-compose.yaml up
