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
  
  # 🚀 Postup nasadenia novej verzie InsuranceApp na Oracle VM

## 1. Úpravy a build na lokále (PC)
1. Uprav kód v IntelliJ alebo inom editore.  
2. Otestuj lokálne:
   ```bash
   mvn spring-boot:run
   ```
3. Vygeneruj nový JAR a Docker image:
   ```bash
   mvn clean package -DskipTests
   docker build -t insuranceapp_app .
   ```
4. Otestuj image lokálne:
   ```bash
   docker run -p 8080:8080 insuranceapp_app
   ```

---

## 2. Prenos na Oracle VM

### 🅰️ Kopírovanie celého projektu
```bash
scp -i ~/.ssh/oracle_key.pem -r ~/Desktop/InsuranceApp ubuntu@141.144.236.66:~/
```
Na VM:
```bash
cd ~/InsuranceApp
docker-compose down
docker-compose up -d --build
```

### 🅱️ Prenos iba JAR súboru (rýchlejšie)
1. Build lokálne:
   ```bash
   mvn clean package -DskipTests
   ```
2. Prenos JAR:
   ```bash
   scp -i ~/.ssh/oracle_key.pem target/app.jar ubuntu@141.144.236.66:~/InsuranceApp/app.jar
   ```
3. Na VM rebuild a spustenie:
   ```bash
   cd ~/InsuranceApp
   docker-compose down
   docker-compose up -d --build
   ```

---

## 3. Overenie nasadenia
- Logy aplikácie:
  ```bash
  docker logs -f springboot_insurance_app
  ```

- Skontrolovať web:  
  👉 `https://insuranceapp.gloziksoft.sk`

---

## 💡 Tipy do praxe
- Použi **Git + pull** namiesto scp → jednoduchšie nasadzovanie.  
- V budúcnosti zváž **CI/CD pipeline** (GitHub Actions, GitLab CI).  
- DB nechaj bežať stále, rebuilduj len appku.

2. Alias v shelli (rýchly trik)

Do ~/.bashrc alebo ~/.zshrc pridaj:

alias oraclevm='ssh -i ~/.ssh/id_rsa ubuntu@141.144.236.66'


Potom stačí:

oraclevm
