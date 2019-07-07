1. extraire le command handler dans la partie eventsourcing (via la notion d'abstraction ou pas...)
Dans ce cas je vais certainement devoir introduire une dependence à javax.entreprise.api...
Compliqué ... car je passe des arguments injecté dans la Command
=> KO

1. finir les tests
1. extraire lib
Comment faire avec FlyWay ???

1. extraire write
1. run local avec docker compose : posgresql version debezium, kafka
1. creer query avec kafka + test Envers... pour kafka utiliser l'api rest pendant les tests pour simuler debezium
1. openshift (jenkins, environnement, test acceptance globale)

Attention: query + write même répository !
microservice notification &vec greenmail !

## infra

generer une lib pour l'eventstore...

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
