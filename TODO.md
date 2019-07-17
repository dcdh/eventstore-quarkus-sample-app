1. extraire le command handler dans la partie eventsourcing (via la notion d'abstraction ou pas...)
Dans ce cas je vais certainement devoir introduire une dependence à javax.entreprise.api...
Compliqué ... car je passe des arguments injecté dans la Command
=> KO

1. mettre en place debezium : OK
1. remplacer Order par Todo :) OK

1. creer un multimodule 
1. passer par une api commune !! todo-api


1. extraire write
1. creer query avec kafka + test Envers... pour kafka utiliser l'api rest pendant les tests pour simuler debezium

1. notification avec envoie mail + peeble dans un greenmail :)
> passer par une base de données pour stocker les events traités !


1. openshift (jenkins, environnement, test acceptance globale)

Attention: query + write même répository !
microservice notification &vec greenmail !

debezium merge les event dans un seul stream !
kafka rest api...

tester peeble + mail dans la notification !
en natif !

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



prio:


3. passage à openshift
-> utiliser streamzi !!!
- jenkins
- script creation env !
- pipeline compilation pour chaque microservice
- pipeline native to prod...
> projet staging
> utilise docker pour initialiser l'env
> run test e2e
> tag vers la production !


pas de joins gris mais des joins epoxy