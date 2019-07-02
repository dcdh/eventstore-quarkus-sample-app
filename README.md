## Postgresql

> docker run --ulimit memlock=-1:-1 -it --rm=true --memory-swappiness=0 --name eventstore -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=eventstore -p 5432:5432 postgres:10.5

> docker exec -it eventstore /bin/bash

> pg_dump -d eventstore -U postgres

> psql -d eventstore -U postgres

> DROP SCHEMA public CASCADE;CREATE SCHEMA public;
