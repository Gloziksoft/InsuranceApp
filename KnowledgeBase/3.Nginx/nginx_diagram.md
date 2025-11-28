                         +---------------------------------------------+
                         |                 Tvoj Linux OS               |
                         |    (Ubuntu / Debian / Arch / server)        |
                         +-----------------------+---------------------+
                                                 |
                                                 |
                                       (beží ako služba)
                                                 |
                         +-----------------------v---------------------+
                         |                 DOCKER DAEMON               |
                         |         - spravuje images/containers        |
                         +-----------------------+---------------------+
                                                 |
                              Docker CLI príkazy |
                                                 v
                         +-----------------------+---------------------+
                         |                   NGINX                     |
                         |---------------------------------------------|
                         | 1) Webserver (statické súbory)             |
                         | 2) Reverse Proxy (proxy_pass → backend)    |
                         | 3) Load Balancer (upstream N backendov)    |
                         | 4) SSL/TLS terminácia                       |
                         +-----------------------+---------------------+
                                                 |
                           docker-compose        |         K8s Ingress
                               routing           |         routing/TLS
                                                 |
                  +------------------------------v------------------------------+
                  |                      APLIKÁCIA                              |
                  |    (Spring Boot, Node.js, PHP, Python… Docker Container)    |
                  +----------------------+---------------------+----------------+
                                         |                     |
                                         |                     |
                                (Docker Network)          (Kubernetes Network)
                                         |                     |
                                         v                     v
           +-----------------------------+-----------------------------+
           |        DOCKER COMPOSE / KUBERNETES (podľa prostredia)    |
           |----------------------------------------------------------|
           | Docker Compose: services, networks, volumes               |
           | Kubernetes: Deployment → ReplicaSet → Pods → Services    |
           +----------------------------------------------------------+


# 1️⃣ Čo je NGINX (najdôležitejšie vety)

Webserver → slúži HTML, CSS, JS, obrázky

Reverse Proxy → preposiela traffic backendu

Load Balancer → rozdeľuje požiadavky medzi viac replík

TLS terminácia → HTTPS, certifikáty (Let’s Encrypt)

Stabilný, extrémne rýchly, veľmi nízka spotreba CPU/RAM

V Kubernetes funguje ako Ingress Controller (router pre celý cluster)

# 2️⃣ NGINX ako Webserver (statika)

Používa sa na hosting:

React/Angular/Vue build

HTML stránok

obrázkov

dokumentácie

Konfigurácia:

server {
    listen 80;
    root /usr/share/nginx/html;
    index index.html;
}

# 3️⃣ NGINX ako Reverse Proxy

Najčastejší scenár v Docker Compose:

Nginx → backend:8080


Konfigurácia:

location /api/ {
    proxy_pass http://backend:8080/;
}


Funkcia:

frontend ide cez NGINX

backend je “schovaný” v internej Docker/K8s sieti

NGINX preposiela, pridáva hlavičky, rieši keep-alive, timeouts

# 4️⃣ NGINX ako Load Balancer

Ručné load-balancing mimo Kubernetes.

upstream app_backend {
    server app1:8080;
    server app2:8080;
}

server {
    listen 80;
    location / {
        proxy_pass http://app_backend;
    }
}


K8s toto robí automaticky cez Service (ClusterIP).

# 5️⃣ SSL / HTTPS / Certifikáty

NGINX je výborný TLS terminátor:

server {
    listen 443 ssl;
    ssl_certificate /etc/nginx/cert.pem;
    ssl_certificate_key /etc/nginx/key.pem;

    location / {
        proxy_pass http://backend:8080;
    }
}


Úlohy:

príjem HTTPS

preposlanie HTTP do backendu

redirect 80 → 443

práca s Let’s Encrypt

# 6️⃣ Caching (voliteľné)

Veľmi výkonné cacheovanie:

proxy_cache my_cache;
proxy_cache_valid 200 10m;


NGINX vie výrazne odľahčiť backend.

# 7️⃣ Rate limiting / throttling

Chráni backend:

limit_req_zone $binary_remote_addr zone=mylimit:10m rate=5r/s;

location /api/ {
    limit_req zone=mylimit;
    proxy_pass http://backend:8080;
}

# 8️⃣ NGINX v Docker prostredí
Frontend hosting:
FROM nginx:alpine
COPY dist/ /usr/share/nginx/html

Custom config:
FROM nginx:alpine
COPY nginx.conf /etc/nginx/nginx.conf

Docker Compose:
nginx:
  image: nginx:alpine
  volumes:
    - ./nginx.conf:/etc/nginx/nginx.conf
  ports:
    - "80:80"

# 9️⃣ NGINX v Kubernetes

NGINX môže byť:

1) obyčajný Pod (napr. statický frontend)
Deployment → Pod → Nginx
Service → port 80

2) Ingress Controller (najdôležitejšie)

Tu NGINX robí:

routing domén (api.example.com)

routing path (/api, /admin)

HTTPS

forwarding na Services

K8s objekt:

Ingress → Service → Pod

# 10️⃣ Stručné porovnanie
Funkcia	Docker Compose	Kubernetes	NGINX
Reverse Proxy	✖️ manuálne	✖️ nepotrebné	✔️
Load Balancing	✖️ manuálne	✔️ Service	✔️
HTTPS	✔️	✔️ (Ingress)	✔️
Routing domén	✖️	✔️ (Ingress)	✔️
Statika	✔️	✔️	✔️
# 11️⃣ Najkratšie a najdôležitejšie pochopenie

NGINX je “brána”, ktorá prijíma všetky requesty a rozhoduje, kam ich pošle:

ak je to statika → pošle súbor

ak je to /api → pošle backendu

ak je viac replík → rozloží traffic

ak je HTTPS → vyrieši certifikát

V Kubernetes sa táto brána nazýva Ingress Controller, ale často je to znovu NGINX.

# 12️⃣ Jednou vetou:

NGINX = rýchly webserver + inteligentný reverse proxy + výkonný load balancer a router, ktorý stojí pred tvojou aplikáciou v Docker-i aj Kubernetes.

                     +-------------------------------------+
                     |              LINUX OS                |
                     |      (Ubuntu / Debian / Server)      |
                     +--------------------+------------------+
                                          |
                                          v
                     +-------------------------------------+
                     |           DOCKER DAEMON             |
                     |   (images • containers • networks)  |
                     +--------------------+------------------+
                                          |
                                          v
                     +-------------------------------------+
                     |           NGINX CONTAINER           |
                     |  Webserver • Reverse Proxy • TLS    |
                     |          • Load Balancer            |
                     +---------+-----------+----------------+
                               |           |
                               |           |
                    (Docker Compose)   (Kubernetes Ingress)
                               |           |
                               v           v
         +---------------------------+   +----------------------------+
         |    DOCKER COMPOSE PATH   |   |     KUBERNETES PATH        |
         | services: nginx • app • db|  | Ingress → Service → Pods   |
         +-------------+-------------+   +-------------+--------------+
                       |                           |
                       +-------------+ +-----------+
                                     v v
                     +-------------------------------------+
                     |        BACKEND APPLICATION          |
                     | Spring Boot • Node.js • Python API  |
                     +--------------------+------------------+
                                          |
                                          v
                     +-------------------------------------+
                     |       NETWORK & ORCHESTRATION       |
                     | Docker networks • volumes           |
                     | K8s Deployments • Pods • Services   |
                     +-------------------------------------+

