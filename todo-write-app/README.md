# todo-query-app

## Tips

> ##### connect to database
> psql -U postgresuser mutable

> ##### remove and delete all running - or not - containers
> docker rm -f $(docker ps -aq)

docker rm -f $(docker ps -aq) && docker-compose up
