# 🚀 Rýchly ťahák – nasadenie a správa **InsuranceApp** na Oracle VM

## 🔑 Pripojenie na Oracle VM
```bash
ssh -i ~/.ssh/oracle_key.pem ubuntu@IP_ADRESA_VM
```
👉 `IP_ADRESA_VM` je napr. `141.144.236.66`  
👉 SSH kľúč je ten, čo máš uložený v Oracle Console.

---

## 📦 Docker Compose – základné príkazy

### Spustiť projekt
```bash
cd ~/InsuranceApp
docker-compose up -d --build
```

### Zastaviť projekt
```bash
cd ~/InsuranceApp
docker-compose down
```

### Zastaviť + vymazať aj dáta (čistý štart DB)
```bash
docker-compose down -v
```

### Zobraziť bežiace kontajnery
```bash
docker ps
```

### Logy aplikácie
```bash
docker logs -f springboot_insurance_app
```

### Logy databázy
```bash
docker logs -f mariadb_container_insurance_app
```

---

## 🗄️ Práca s databázou (MariaDB)

### Otvoriť SQL konzolu v DB
```bash
docker exec -it mariadb_container_insurance_app mariadb -uroot -proot
```

### Vybrať databázu
```sql
USE insurance_app;
SHOW TABLES;
```

### Naimportovať dump manuálne
```bash
docker exec -i mariadb_container_insurance_app mariadb -uroot -proot insurance_app < ~/InsuranceApp/db-init-scripts/init.sql
```

---

## 🌍 Aplikácia a phpMyAdmin

- Aplikácia:  
  👉 `http://141.144.236.66:8080`  
  👉 alebo po DNS nastavení `http://insuranceapp.gloziksoft.sk`

- phpMyAdmin:  
  👉 `http://141.144.236.66:8081`  
  👉 alebo po DNS `http://insuranceapp.gloziksoft.sk:8081`

---

## 🔒 HTTPS (Let’s Encrypt cez Certbot + Nginx Proxy)

1. Nainštaluj Nginx + Certbot:
   ```bash
   sudo apt update && sudo apt install nginx certbot python3-certbot-nginx -y
   ```

2. Nastav reverzný proxy pre Spring Boot (`/etc/nginx/sites-available/insuranceapp`):
   ```nginx
   server {
       server_name insuranceapp.gloziksoft.sk;

       location / {
           proxy_pass http://localhost:8080;
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
           proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
           proxy_set_header X-Forwarded-Proto $scheme;
       }
   }
   ```

3. Aktivuj konfiguráciu:
   ```bash
   sudo ln -s /etc/nginx/sites-available/insuranceapp /etc/nginx/sites-enabled/
   sudo nginx -t
   sudo systemctl reload nginx
   ```

4. Získaj HTTPS certifikát:
   ```bash
   sudo certbot --nginx -d insuranceapp.gloziksoft.sk
   ```

👉 Hotovo, budeš mať `https://insuranceapp.gloziksoft.sk`

---

## 🛠️ Údržba a správa

- Reštartovať VM:
  ```bash
  sudo reboot
  ```
- Aktualizácie systému:
  ```bash
  sudo apt update && sudo apt upgrade -y
  ```
- Kontrola logov systému:
  ```bash
  journalctl -xe
  ```
