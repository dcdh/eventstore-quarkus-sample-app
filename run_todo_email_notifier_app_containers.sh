#!/bin/bash
docker kill $(docker ps -aq); docker rm $(docker ps -aq); docker-compose -f todo-email-notifier-app/docker-compose.yaml up
