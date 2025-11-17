

📘 PRÍRUČKA: Kompletné nasadenie aplikácie na Oracle Cloud VM s NGINX + HTTPS + Docker

(vrátane riešenia zmeny Public IP, DNS, firewallu, reverse proxy a SSL certifikátov)

🧩 OBSAH

1️⃣ Vytvorenie Oracle VM
2️⃣ Pripojenie k VM (SSH)
3️⃣ Základná konfigurácia servera
4️⃣ Inštalácia Docker + Docker Compose
5️⃣ Nasadenie aplikácie (port 8080)
6️⃣ Inštalácia a konfigurácia NGINX
7️⃣ Nastavenie reverznej proxy
8️⃣ Nastavenie firewallu (iptables + Oracle NSG)
9️⃣ Nastavenie DNS (A záznam)
🔟 Aktivácia HTTPS (Let's Encrypt Certbot)
1️⃣1️⃣ Oprava chýb po zmene public IP (Oracle)
1️⃣2️⃣ Finálna konfigurácia NGINX
1️⃣3️⃣ Testovanie služby
1️⃣4️⃣ Diagnostika a audit servera

🟦 1. Vytvorenie Oracle VM

Pri vytváraní inštancie sa nastaví:

OS: Ubuntu 22.04/24.04

Public Subnet

Public IPv4 – Ephemeral

SSH kľúč pre prístup

NSG (Network Security Group) → porty 22, 80, 443, 8080

🟦 2. Pripojenie do VM (SSH)

V ~/.ssh/config:

Host insurance
    HostName <PUBLIC_IP>
    User ubuntu
    IdentityFile ~/.ssh/oracle_key


Pripojenie:

ssh insurance

🟦 3. Základná konfigurácia servera

Update balíkov:

sudo apt update && sudo apt upgrade -y

🟦 4. Inštalácia Docker + Docker Compose
sudo apt install docker.io -y
sudo systemctl enable docker --now


Test:

docker ps

🟦 5. Nasadenie aplikácie (port 8080)

Tvoja aplikácia beží v Dockeri.
Napr.:

docker run -p 8080:8080 --name app backend-image


Overenie:

curl http://localhost:8080

🟦 6. Inštalácia NGINX
sudo apt install nginx -y
sudo systemctl enable nginx --now


Test:

curl http://localhost

🟦 7. Reverzná proxy – základná konfigurácia

Vytvoríme nový config:

sudo nano /etc/nginx/sites-available/insuranceapp

🟦 8. Firewall (iptables + Oracle NSG)
A) Oracle Network Security Group musí mať povolené:
Port	Protokol	Účel
22	TCP	SSH
80	TCP	HTTP
443	TCP	HTTPS
8080	TCP	Aplikácia v Dockeri
B) Lokálny firewall na VM – povolenie portov
sudo iptables -A INPUT -p tcp --dport 80 -j ACCEPT
sudo iptables -A INPUT -p tcp --dport 443 -j ACCEPT


Uloženie pravidiel:

sudo apt install iptables-persistent -y
sudo netfilter-persistent save

🟦 9. DNS – A záznam domény

V DNS musí byť:

insuranceapp.gloziksoft.sk → 158.180.19.25

🟦 10. Aktivácia HTTPS – Certbot

Inštalácia:

sudo apt install certbot python3-certbot-nginx -y


Vygenerovanie certifikátu:

sudo certbot --nginx -d insuranceapp.gloziksoft.sk


Certbot:

overí, že port 80 funguje

vytvorí HTTPS blok

uloží certifikáty

nastaví auto-renew

🟦 11. Oprava chýb po zmene Public IP (Oracle Bug)

Oracle VM niekedy dostane novú public IP, čím sa rozbije:

❌ DNS
❌ SSH prístup
❌ NGINX server_name
❌ reverse proxy
❌ certbot challenge
❌ firewall pravidlá

Riešili sme:

Aktualizácia DNS

Oprava SSH konfigurácie lokálne

Odstránenie starých host keys:

ssh-keygen -R <stara_IP>
ssh-keygen -R <domena>


Kontrola novej IP:

curl -s ifconfig.me


Oprava firewallu, ktorý blokoval port 80

Vyčistenie NGINX configu

🟦 12. Finálna konfigurácia NGINX po všetkých opravách

📌 /etc/nginx/sites-available/insuranceapp

✅ 1. HTTPS (doména)
server {
    listen 443 ssl;
    listen [::]:443 ssl;

    server_name insuranceapp.gloziksoft.sk;

    ssl_certificate /etc/letsencrypt/live/insuranceapp.gloziksoft.sk/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/insuranceapp.gloziksoft.sk/privkey.pem;
    include /etc/letsencrypt/options-ssl-nginx.conf;
    ssl_dhparam /etc/letsencrypt/ssl-dhparams.pem;

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}

✅ 2. HTTP → HTTPS redirect (doména)
server {
    listen 80;
    listen [::]:80;
    server_name insuranceapp.gloziksoft.sk;

    return 301 https://insuranceapp.gloziksoft.sk$request_uri;
}

✅ 3. IP adresa – BEZ presmerovania (nesmie redirectovať!)
server {
    listen 80;
    listen [::]:80;
    server_name 158.180.19.25;

    location / {
        proxy_pass http://127.0.0.1:8080;
    }
}

🟦 13. Testovanie služby

HTTP IP:

curl -I http://158.180.19.25


HTTP doména:

curl -I http://insuranceapp.gloziksoft.sk


HTTPS doména:

curl -I https://insuranceapp.gloziksoft.sk


NGINX syntax check:

sudo nginx -t
sudo systemctl reload nginx

🟦 14. Diagnostika, logy a audit
NGINX logy
sudo tail -f /var/log/nginx/access.log
sudo tail -f /var/log/nginx/error.log

Docker kontajnery
docker ps
docker logs <container>

Otvorené porty
sudo ss -tulpn

Firewall
sudo iptables -L -n

🎉 ZÁVER – čo teraz funguje

Tvoj server má teraz:

✔ funkčnú doménu
✔ funkčnú IP adresu
✔ HTTPS certifikáty
✔ správne reverse proxy
✔ správne presmerovanie HTTP → HTTPS
✔ firewall povolený
✔ docker bežiaci na porte 8080
✔ nginx ako produkčný front-end
✔ automatické obnovovanie SSL
✔ opravené problémy po zmene public IP
✔ konfiguráciu, ktorá prežije reboot





📘 ČO SA PRESNE STALO — celý príbeh stručne na začiatok

Tvoja VM bola OK, nginx bol OK, DNS bolo OK, Oracle sieť bola OK.

Ale vo vnútri VM bol Linux firewall (iptables) nastavený tak, že blokoval všetky porty okrem 22 a 80.

HTTPS (443) nefungoval, lebo nebol povolený v iptables.

Python server na porte 8081 nefungoval, pretože port 8081 tiež zablokoval iptables.

Dokonca aj lokálne pripojenie na porty zlyhalo — pretože firewall blokoval komunikáciu ešte predtým, než sa dostala k nginxu alebo python serveru.

Keď sme pridali ACCEPT pravidlá pre porty 80/443/8080/8081, dali sme ich až za REJECT, takže stále nefungovali.

Keď sme ich presunuli nad REJECT, všetko sa okamžite rozbehlo.

🧨 HLAVNÁ PRÍČINA PROBLÉMU
🔥 Oracle Linux Ubuntu image má default firewall pravidlo:
REJECT all -- reject-with icmp-host-prohibited


Toto znamená:

„Zahoď všetky prichádzajúce requesty na porty, ktoré nie sú explicitne povolené.“

A čo bolo povolené?

22 (SSH)

80 (HTTP)

🔥 Port 443 NEBOL povolený.
🔥 Port 8081 NEBOL povolený.
🔥 Port 8080 bol len pre docker NAT, nie pre INPUT.

Výsledok:

✔ Ping fungoval
❌ HTTPS nefungoval
❌ Python nefungoval
❌ Docker externý port nefungoval
❌ Dokonca ani curl localhost:8081 nefungoval (!)

Toto je presne správanie, ktoré si videl.

⚙️ ČO SME UROBILI – krok za krokom

Tu je presná diagnostika a riešenie, aby si to vedel použiť nabudúce:

🔍 1) Diagnostika: iptables výpis

Spustili sme:

sudo iptables -L -n -v


A tam sme uvideli:

REJECT all -- reject-with icmp-host-prohibited


na konci INPUT chainu.

A VŠETKY ACCEPT PORTY BOLI POD TÝM.

To znamená:

➡️ Linux firewall ignoroval tvoje povolené porty
➡️ VŠETKY PORTY OKREM 22 A 80 sa zahadzovali

🧪 2) Test Python servera

Chcel som overiť, či je problém „na sieti“ alebo „v systéme“.
Preto sme spustili:

sudo python3 -m http.server 8081


a lokálny test:

curl http://localhost:8081


A toto ZLYHALO.

Toto je rozhodujúci dôkaz:

❗ Keď localhost → localhost nefunguje, problém je 100 % firewall na VM.

Nie Oracle, nie DNS, nie nginx → len firewall na serveri.

🔧 3) Pridali sme ACCEPT pravidlá

Najprv si pridal:

sudo iptables -A INPUT -p tcp --dport 443 -j ACCEPT


Ale to sa pridalo na koniec INPUT chainu:

REJECT
ACCEPT 443


🔥 A keďže firewall číta zhora nadol → REJECT to zablokoval skôr, než sa dostal k ACCEPT.

🛠️ 4) Preto sme to opravili správne: INSERT
Pridali sme pravidlá NA VRCH:
sudo iptables -I INPUT 1 -p tcp --dport 80 -j ACCEPT
sudo iptables -I INPUT 1 -p tcp --dport 443 -j ACCEPT
sudo iptables -I INPUT 1 -p tcp --dport 8080 -j ACCEPT
sudo iptables -I INPUT 1 -p tcp --dport 8081 -j ACCEPT

-I INPUT 1 = vložiť pravidlo na prvú pozíciu.
Teraz je poradie:
ACCEPT 80
ACCEPT 443
ACCEPT 8080
ACCEPT 8081
REJECT all

💥 A TÝM SA VŠETKO OTVORILO.

🎉 5) Overenie


curl localhost funguje


python server funguje


nginx funguje


HTTPS funguje


doména sa načítava


web funguje zvonka


Problém vyriešený.

🛡️ 6) Uloženie firewall pravidiel
Aby po reštarte nezmizli, odporučil som:
sudo apt install iptables-persistent -y
sudo netfilter-persistent save


📘 ZHRNUTIE PRE TVOJE POZNÁMKY
Tu je stručný zápis, ktorý si môžeš uložiť:

📝 Ako diagnostikovať, že porty nefungujú na Linux VM


Skús lokálne:
curl http://localhost:PORT



ak to nefunguje → problém je firewall v systéme, nie DNS/sieť.




Skontroluj iptables:
sudo iptables -L -n -v



Hľadaj pravidlá typu:
REJECT all



Dôležité: ACCEPT pravidlá musia byť NAD REJECT.
Inak sa nikdy nevykonajú.



🛠️ Ako povoliť porty v iptables
Správne je:
sudo iptables -I INPUT 1 -p tcp --dport PORT -j ACCEPT

NESPRÁVNE je:
sudo iptables -A INPUT -p tcp --dport PORT -j ACCEPT

Lebo -A (append) dá pravidlo na koniec, kde je často REJECT.

🔒 Ako uložiť iptables
sudo apt install iptables-persistent
sudo netfilter-persistent save


📌 Najčastejší dôvod, prečo nefunguje HTTPS
Port 443 nie je v iptables povolený
(najčastejšie na Oracle/Hetzner/Contabo default Ubuntu image).

🔥 A HOTOVO. Máš to teraz krásne, detailne vysvetlené.
Ak chceš, môžem ti ešte:

