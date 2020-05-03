#!/bin/bash
docker kill $(docker ps -aq); docker rm $(docker ps -aq); docker-compose -f docker-compose-test.yaml up
