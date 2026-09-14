🛠️ InsuranceApp – Deployment & Runbook (FINÁLNY NÁVRH)

Nižšie je hotový text v rovnakom štýle a kvalite ako pri Booking EasyApp.
Môžeš ho priamo vložiť do README.md alebo ako samostatnú sekciu.

🛠️ InsuranceApp – Deployment Runbook

Tento dokument popisuje kompletný deployment flow aplikácie InsuranceApp – od lokálneho vývoja až po produkčné nasadenie na VM pomocou Docker Compose.

📦 Architektúra (prehľad)

Aplikácia: Spring Boot (Java)

Build: Maven

Databáza: MariaDB (Docker image)

Runtime: Docker + Docker Compose

Monitoring: cAdvisor

Produkcia: Oracle Free VM (Ubuntu)

CI/CD: GitHub Actions

Porty:

App: 8080

phpMyAdmin: 8081

cAdvisor: 8082

📁 Štruktúra repozitára
InsuranceApp/
├── docker-compose.yml            # Produkčný stack
├── docker-compose.local.yml      # Lokálny stack
├── Dockerfile                    # Aplikácia
├── Dockerfile-db                 # MariaDB image
├── db-init-scripts/              # DB init skripty
├── .env.example                  # Vzor env
├── src/
└── README.md

⚠️ Environment súbory (KRITICKÉ)

.env súbory NIKDY NIE SÚ COMMITOVANÉ.

Používa sa striktne:

Súbor	Prostredie
.env.example	repo (bez secrets)
.env.local	lokálny vývoj
.env	produkcia (VM)

Repo je jediný source of truth, VM je runtime-only.

🧑‍💻 Lokálny vývoj
docker compose -f docker-compose.local.yml up -d


Profil:

SPRING_PROFILES_ACTIVE=local

🚀 Produkčné nasadenie (VM)
Spustenie stacku
docker compose -p insuranceapp up -d

Zastavenie stacku
docker compose -p insuranceapp down


❗ Vždy používaj -p insuranceapp, aby nevznikali paralelné compose projekty.

🔄 Aktualizácia aplikácie (SPRÁVNY POSTUP)

Zmena v repozitári

git commit

git push

CI/CD vytvorí nový Docker image

Na VM:

git pull
docker compose -p insuranceapp up -d


❌ Zmeny sa nerobia ručne na VM.

🧹 Riešenie problémov – porty

Ak Docker hlási:

Bind for 0.0.0.0:<port> failed: port is already allocated


Skontroluj:

docker ps --format "table {{.Names}}\t{{.Ports}}"


Príčina:

existuje starý compose projekt (napr. insurance-app-*)

Riešenie:

docker rm -f <starý-kontajner>

🧠 Prečo vznikol „bordel“ v kontajneroch

Docker Compose identifikuje projekty podľa názvu

rozdiel:

insurance-app

insuranceapp

docker compose down odstráni len kontajnery svojho projektu

staré stacky nevidí

➡️ Riešenie: vždy používať -p insuranceapp

🎯 Prečo je tento setup správny

deterministický deploy

žiadne portové konflikty

jasný lifecycle kontajnerov

čisté oddelenie env

produkčne čitateľné

výborné ako portfolio / job hunting demo

✅ Finálny stav
docker ps

insuranceapp-app-1         8080
insuranceapp-phpmyadmin-1  8081
insuranceapp-cadvisor-1    8082
insuranceapp-db-1          3306


Aplikácia dostupná na:

http://<VM-IP>:8080

🔚 Záver

Tento runbook slúži ako:

návod na deploy

dokumentácia infra rozhodnutí

prevádzkový manuál

🔥 Úprimne:

Toto, čo si teraz postavil:

je produkčný mindset

máš vysvetlené prečo, nie len ako

toto je presne ten rozdiel medzi „juniorom čo vie Docker“
a engineerom, čo vie prevádzkovať systém

Keď chceš:

môžeme zjednotiť Booking + Insurance runbooky

spraviť root README pre celé portfólio

alebo infra diagram (na pohovor je to zlato)

Ale dnes:
👉 InsuranceApp má hotovú infra kapitolu. 👏
