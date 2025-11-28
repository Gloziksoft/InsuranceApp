                           +---------------------------+
                           |       Tvoj Linux OS       |
                           | (Ubuntu / Debian / Arch..)|
                           +-------------+-------------+
                                         |
                                         |
                                 (beží ako služba)
                                         |
                           +-------------v-------------+
                           |      DOCKER DAEMON        |
                           |        (dockerd)          |
                           |  - spravuje images        |
                           |  - spúšťa kontajnery      |
                           |  - vytvára siete          |
                           |  - pracuje s volume       |
                           +------+---------------------+
                                  ^
                                  |
                   (príkazy)      |      (odpovede)
                                  |
                     +------------+------------+
                     |        Docker CLI       |
                     |      (docker run ...)   |
                     +------------+------------+
                                  |
                                  | požiadavka na vytvorenie image
                                  |
                         +--------v--------+
                         |   Dockerfile    |
                         | (recept)        |
                         | FROM openjdk:17 |
                         | COPY app.jar    |
                         | ENTRYPOINT ...  |
                         +--------+--------+
                                  |
                                  | docker build
                                  v
                         +--------+--------+
                         |   Docker Image   |
                         |  (hotová šablóna)|
                         |  filesystem +    |
                         |  Java + JAR      |
                         +--------+---------+
                                  |
                                  | docker run
                                  v
                     +------------+-------------+
                     |      Docker Container    |
                     |  (bežiaci proces)        |
                     |                          |
                     |  izolácie:               |
                     |   - filesystem           |
                     |   - sieť                 |
                     |   - procesy              |
                     |   - hostname             |
                     +------------+-------------+
                                  |
                                  |
                        (ak je súčasťou Compose)
                                  |
                                  v
         +--------------------------------------------------+
         |                 Docker Compose                    |
         |   orchestrace viacerých kontajnerov               |
         |                                                  |
         |  services:                                       |
         |   app:    ---> spustí kontajner z image          |
         |   db:     ---> spustí mariadb kontajner          |
         |   redis:  ---> ďalší kontajner                   |
         |                                                  |
         |  spoločná sieť:                                  |
         |    - app --> db (dns: "db:3306")                 |
         |    - db  --> app (dns: "app:8080")               |
         |                                                  |
         |  volume:                                         |
         |    - persistencia DB                             |
         |                                                  |
         +-----------------------+--------------------------+
                                 |
                                 |
                        +--------v--------+
                        |   Network       |
                        |  (bridge driver)|
                        |  - vlastné IP   |
                        |  - DNS mená     |
                        +-----------------+

🤯 ČO TI DIAGRAM HOVORÍ (v krátkosti)
1️⃣ Dockerfile

→ len RECEPT
→ ešte nič nebeží

2️⃣ Docker build

→ daemon z toho spraví IMAGE

3️⃣ Docker run

→ daemon z image spustí PROCESS = CONTAINER

4️⃣ Docker Daemon

→ je „motor“, ktorý všetko riadi
→ jediná časť, ktorá je nainštalovaná v OS

5️⃣ Docker CLI

→ sú len príkazy, ktoré posielajú požiadavky daemonu

6️⃣ Docker Compose

→ orchestruje viac kontajnerov naraz
→ vytvorí network
→ mená ako db, app fungujú ako DNS

7️⃣ Kontajner = proces

→ beží izolovane
→ má vlastný FS, IP, porty

🧠 Jednou vetou:

Docker Daemon (program v OS)
→ vytvorí image (šablónu)
→ spustí container (proces v izolácii)
→ a Docker Compose spravuje viac takýchto kontajnerov naraz.
