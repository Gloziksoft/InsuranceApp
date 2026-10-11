# Insurance VM – Docker Port Binding Hardening

**Date:** 2026-09-29
**Environment:** Insurance VM (`insuranceapp`)
**Purpose:** Final documentation of Point 4 – restricting Docker host port bindings to the intended interfaces and preserving private VM-to-VM monitoring access.

---

## 1. Context

Point 4 followed the previous network-security hardening work on the Insurance VM.

The goal was to make the Docker port bindings explicit:

- Insurance App: accessible through localhost and the VM private IP.
- cAdvisor: accessible through the VM private IP only.
- phpMyAdmin: accessible through localhost only.
- Insurance DB: no host port published.

This keeps the application reachable through the normal NGINX path while allowing the Booking VM's Prometheus to collect metrics over the private network.

---

## 2. Target configuration

Final intended bindings:

```yaml
app:
  ports:
    - "127.0.0.1:8080:8080"
    - "10.0.0.38:8080:8080"

cadvisor:
  ports:
    - "10.0.0.38:8082:8080"

phpmyadmin:
  ports:
    - "127.0.0.1:8081:80"

# db: no host port published
```

The Insurance VM private IP is `10.0.0.38`.

The active Docker network is `insuranceapp_insurance_network` (`172.18.0.0/16`).

---

## 3. Local Compose validation

The modified `docker-compose.yml` was validated with:

```bash
docker compose config
```

The resulting configuration confirmed:

- App published on `127.0.0.1:8080` and `10.0.0.38:8080`
- cAdvisor published on `10.0.0.38:8082`
- phpMyAdmin published on `127.0.0.1:8081`
- DB has no published host port

The Git diff contained only the planned port-binding changes:

```diff
- "8080:8080"
+ "127.0.0.1:8080:8080"
+ "10.0.0.38:8080:8080"

- "8082:8080"
+ "10.0.0.38:8082:8080"
```

---

## 4. Git and CI/CD

The repository change was committed as:

```text
eebdb81 Restrict Insurance app and cAdvisor bindings
```

GitHub Actions workflow `Build and Push Insurance App #117` completed successfully.

The existing workflow redeploys the Insurance app only:

```bash
docker compose pull
docker compose up -d --force-recreate --no-deps app
```

The workflow was intentionally not changed.

Because the cAdvisor binding is infrastructure configuration rather than an application image deployment, cAdvisor was recreated manually on the Insurance VM with:

```bash
docker compose up -d --force-recreate --no-deps cadvisor
```

---

## 5. Final Docker state

After applying the changes, `docker ps` showed:

```text
insuranceapp-app-1
  10.0.0.38:8080->8080/tcp
  127.0.0.1:8080->8080/tcp

insuranceapp-phpmyadmin-1
  127.0.0.1:8081->80/tcp

insuranceapp-db-1
  3306/tcp

insuranceapp-cadvisor-1
  10.0.0.38:8082->8080/tcp
```

The database therefore has no published host port.

---

## 6. Insurance VM local tests

The following tests succeeded on the Insurance VM:

### Application – localhost

```bash
curl -I http://127.0.0.1:8080
```

Result: `HTTP/1.1 200`

### Application – private IP

```bash
curl -I http://10.0.0.38:8080
```

Result: `HTTP/1.1 200`

### cAdvisor – private IP

```bash
curl -I http://10.0.0.38:8082
```

Result: `HTTP/1.1 307 Location: /containers/`

### cAdvisor metrics

```bash
curl -s http://10.0.0.38:8082/metrics | head
```

returned Prometheus metrics successfully.

---

## 7. Cross-VM verification

Tests were performed from the Booking VM (`10.0.0.54`) to the Insurance VM (`10.0.0.38`).

### Booking VM → Insurance App

```bash
curl -I --connect-timeout 5 http://10.0.0.38:8080
```

Result: `HTTP/1.1 200`

### Booking VM → Insurance cAdvisor

```bash
curl -I --connect-timeout 5 http://10.0.0.38:8082
```

Result: `HTTP/1.1 307 Location: /containers/`

### Booking VM → Insurance cAdvisor metrics

```bash
curl -s --connect-timeout 5 http://10.0.0.38:8082/metrics | head
```

returned Prometheus metrics successfully.

---

## 8. Prometheus verification

Prometheus runs on the Booking VM.

The Insurance targets were verified through the Prometheus API:

```text
insurance-app
http://10.0.0.38:8080/actuator/prometheus
up

insurance-cadvisor
http://10.0.0.38:8082/metrics
up
```

This confirms that the central Prometheus instance on the Booking VM can successfully scrape both the Insurance application and Insurance cAdvisor through the private network.

---

## 9. Final architecture relevant to Point 4

```text
                 BOOKING VM
                  10.0.0.54
                     |
                     | private network
                     v
              +--------------+
              |  Prometheus  |
              +--------------+
                |          |
                v          v
       10.0.0.38:8080   10.0.0.38:8082
              |                 |
              v                 v
       Insurance App         cAdvisor
          (metrics)          (metrics)

              INSURANCE VM
               10.0.0.38

       127.0.0.1:8081
              |
              v
          phpMyAdmin

       Docker network
       172.18.0.0/16
              |
              v
        Insurance DB
       (no host port)
```

The application itself is normally accessed through NGINX/HTTPS. The private IP bindings are used for internal VM-to-VM communication and monitoring.

---

## 10. Cleanup

The temporary Point 4 backup `docker-compose.yml.bak-2026-09-29-point4` was removed after successful verification.

The active file remains:

```text
/opt/insurance-app/docker-compose.yml
```

The separate NGINX Point 3 backup was not removed.

---

## 11. Point 4 status

**Point 4 – Docker Port Binding Hardening: COMPLETED**

Verified:

- Docker bindings are explicit.
- Insurance App is available locally and through the private VM IP.
- cAdvisor is available through the private VM IP.
- phpMyAdmin is restricted to localhost.
- Insurance DB has no published host port.
- Booking VM can reach the Insurance App privately.
- Booking VM can reach Insurance cAdvisor privately.
- Booking Prometheus reports both Insurance targets as `up`.
- Git commit and GitHub Actions deployment completed successfully.
- Temporary Point 4 backup was removed.

This document records the completed Point 4 state. The subsequent full architecture review will verify the complete Insurance VM independently.
