1. eventstore


Prio:

tests !!!
avec BDD !!!

TODO : native !!!

TODO extraire en librairie

bdd cucumber domain


est ce que j'aurais pu avoir ce schema
- aggregat
-> version correspondant au nombre d'element dans la liste ?
-> internal version lié à JPA ???
- liste d'event sur l'aggregat

faire les tests
faire un test avec database pour la serialization


...je devrais creer un evenstore autonome...

1. model backenduser
1. consumer rest
1. producer kafka


regarder l'archive wineforex dans le google drive !!!!

faire un endpoint avec un test sur getConstructeur new Instance
faire un endpoint pour serialiser une map !!!

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
