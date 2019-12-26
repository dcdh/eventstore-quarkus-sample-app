> docker rm $(docker ps -aq) && docker-compose up

> http://localhost:9991/#/cluster/default/topic/n/event/data

> docker exec -it todoqueryapp_todo-query_1 /bin/bash

> pg_dump -d todo-query -U postgres

> psql -d todo-query -U postgres

> DROP SCHEMA public CASCADE;CREATE SCHEMA public;
