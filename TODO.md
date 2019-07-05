1. eventstore


Prio:

pour faire mes tests je devrai passer injection par constructor !

- tests !!!
- avec BDD !!!
- native !!!

TODO extraire en librairie

kafka (via debezium)
query

est ce que j'aurais pu avoir ce schema
- aggregat
-> version correspondant au nombre d'element dans la liste ?
-> internal version lié à JPA ???
- liste d'event sur l'aggregat

1. model backenduser
1. consumer rest
1. producer kafka

## infra

generer une lib pour l'eventstore...

je dois stocker les payload en json...
metadata en json... metadata via cle valeur ???


jpa
postgresql
jsonb
flyway
docker
kafka
rest
JenkinsFile


## documentation

format messages emis dans kafka


Pense bete
actif:
acheteur avec manageBy
vendeur avec manageBy
dans le manageBy tu as si actif, ... (voir le status du coup tu peux le deduire si actif via le status...)
