                         +-----------------------------------+
                         |           KUBERNETES CLUSTER       |
                         |    (nodes, networking, storage)    |
                         +-------------------+-----------------+
                                             |
                                             |
                              +--------------v-------------+
                              |        K8s API SERVER       |
                              |  (mozog celého systému)    |
                              +--------------+-------------+
                                             |
                                             |
       +-------------------------------------+------------------------------------+
       |                                                                           |
+------+--------+                                                        +----------+-------+
|   Deployment  |                                                        |   Service        |
| (počet replík)|                                                        | (stable IP/DNS)  |
|  yaml config  |                                                        | ClusterIP/LB/NPort|
+------+--------+                                                        +----------+-------+
       |                                                                           |
       | riadi a vytvára                                                           | smeruje traffic
       v                                                                           v
+------+--------------+                                               +-------------+--------------+
|   ReplicaSet       |                                               |    Endpoints (Pod zoznam)  |
|  (automaticky)     |                                               +-------------+--------------+
+------+--------------+                                                             |
       | repliky                                                                    |
       v                                                                             v
+------+--------------------------+                                    +-------------+--------------+
|               POD               |                                    |               POD          |
| (najmenšia jednotka v K8s)      |                                    |        (ďalšia replika)    |
|---------------------------------|                                    |----------------------------|
|  +---------------------------+  |                                    | +------------------------+ |
|  |      Container (app)     |  |                                    | |   Container (app)      | |
|  |  docker image beží tu    |  |                                    | |  docker image beží tu  | |
|  +---------------------------+  |                                    | +------------------------+ |
|                                 |                                    |                            |
+---------------------------------+                                    +----------------------------+
       ^                                     K8s network                          ^
       |                                     (100% automatická)                   |
       |                                                                             |
       +--------------------------------+-----------------+-------------------------+
                                            |                 |
                                            |                 |
                                +-----------v---+        +----v----------------+
                                |   ConfigMap   |        |      Secret         |
                                | (nekritické    |        | (heslá, prístupy)  |
                                |  nastavenia)   |        | (base64 + secure)  |
                                +---------------+        +---------------------+
                                        |                         |
                                        | mount/env               | mount/env
                                        v                         v
                                +---------------+         +---------------+
                                |     POD       | <-------|     POD       |
                                | (App config)  |         | (App secrets) |
                                +---------------+         +---------------+

   
                         +----------------------------------------------------+
                         |                       Ingress                      |
                         | (domény, HTTPS, routing: /api → service app)       |
                         +-------------------------+--------------------------+
                                                   |
                                                   |
                                                   v
                                         +---------+---------+
                                         |    Service (LB)   |
                                         | external traffic  |
                                         +---------+---------+
                                                   |
                                                   v
                                         +---------+----------+
                                         |        Pods         |
                                         +----------------------+


🟡 1) POD

Najmenšia jednotka v Kubernetes.
Obsahuje 1 alebo viac kontajnerov.

➡️ Pod = balíček, kde bežia tvoj Spring Boot kontajner.

🟢 2) CONTAINER

Toto je to, čo už poznáš z Docker-u.

➡️ Je to Docker image SPUSTENÝ v Pod-e
➡️ Kubernetes používa Docker / containerd na spustenie kontajnera

🔵 3) DEPLOYMENT (najdôležitejší objekt!)

Deployment:

hovorí koľko replík chceš

robí rollout nových verzií

robí rollback pri chybe

udržiava desired state

štartuje / reštartuje Pody

➡️ Deployment = ORCHESTRÁTOR aplikácie
➡️ v Compose je to „services: app:“
➡️ ale 100× inteligentnejší

🟣 4) SERVICE

Pozor: Kubernetes Service nie je to isté ako systemd service.

K8s Service:

stabilná IP adresa v clustri

DNS meno → app-service

smeruje traffic na správne Pody

load-balancing medzi replikami

➡️ Pod = mení sa
➡️ Service = ostáva rovnaká adresa

Presne preto sa Spring pripája na:

jdbc:mariadb://mariadb-service:3306

🔶 5) CONFIGMAP

bežná konfigurácia

bez hesiel

text, yaml, porty, názvy

➡️ v K8s reprezentuje to, čo v Springe poznáš ako application-prod.properties.

🔑 6) SECRET

heslá

DB prístupy

API tokeny

SMTP heslá

JWT key

➡️ Kubernetes ich drží bezpečne (Base64 + možnosť šifrovania)

🟣 7) INGRESS

spravuje domény

certifikáty (TLS)

HTTPS

smerovanie podľa URL (/api, /admin)

➡️ V Compose žiadny Ingress nie je.

⭐ VEĽMI DÔLEŽITÉ:
Kubernetes je ako Docker Compose, ale:

automatický

samoliečiaci

škálovateľný

bez jedného servera

rozložený na nody

s kontrolou verzií rolloutov
