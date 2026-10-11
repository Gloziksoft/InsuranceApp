# Insurance VM – Oracle Cloud Network Security Hardening

**Date:** 2026-09-27
**Environment:** Insurance VM (`insuranceapp`)
**Purpose:** Document the final state after reviewing and closing direct public access to internal container ports.

---

## 1. Context

The Insurance application runs on an Oracle Cloud VM and is exposed publicly through NGINX.

The intended public entry point is:

```text
Internet
   |
   | HTTPS :443
   v
 NGINX
   |
   +----> Insurance Spring Boot application :8080
   |
   +----> phpMyAdmin :8081
```

Docker containers still listen on their VM ports. The important change is that these internal ports are no longer directly reachable from the Internet.

This work was performed as part of the review of Jiří's technical points.

---

## 2. Initial audit

Before changing the configuration, the listening ports on the Insurance VM were checked:

```bash
sudo ss -tulpn | grep -E ':8080|:8081|:8082'
```

The result showed Docker listening on:

```text
0.0.0.0:8080
0.0.0.0:8081
0.0.0.0:8082
```

and corresponding IPv6 listeners.

### Services

| Port | Service |
|---:|---|
| 8080 | Insurance Spring Boot application |
| 8081 | phpMyAdmin |
| 8082 | cAdvisor |

---

## 3. Local VM verification before the change

The services themselves were working locally on the VM.

### Spring Boot

```bash
curl -I http://127.0.0.1:8080
```

Result:

```text
HTTP/1.1 200
```

### phpMyAdmin

```bash
curl -I http://127.0.0.1:8081
```

Result:

```text
HTTP/1.1 200 OK
Server: Apache/2.4.68 (Debian)
X-Powered-By: PHP/8.3.33
```

### cAdvisor

```bash
curl -I http://127.0.0.1:8082
```

Result:

```text
HTTP/1.1 307 Temporary Redirect
Location: /containers/
```

This confirmed that the three services were running correctly before network hardening.

---

## 4. Public access audit before the change

The public IP of the Insurance VM is:

```text
158.180.19.25
```

The application itself was directly reachable:

```bash
curl -I --connect-timeout 5 http://158.180.19.25:8080
```

Result before the change:

```text
HTTP/1.1 200
```

This demonstrated that the Spring Boot application was directly accessible from the Internet, bypassing NGINX.

Ports 8081 and 8082 were tested as well. They were not reachable from the external machine and timed out.

---

## 5. Oracle Cloud network configuration

The Insurance VM uses the subnet:

```text
insurance-subnet
```

The subnet uses the default Security List for:

```text
insurance-vcn
```

The Security List was already limited to the following public ingress ports:

```text
80
443
22
```

It did not contain public ingress rules for 8080, 8081 or 8082.

However, the VM was also associated with a Network Security Group:

```text
ig-quick-action-NSG
```

The NSG contained a public ingress rule for:

```text
TCP 8080
Source: 0.0.0.0/0
```

This rule allowed direct public access to the Insurance Spring Boot application.

---

## 6. Change performed

The unnecessary public ingress rule for TCP port 8080 was removed from:

```text
ig-quick-action-NSG
```

After the change, the relevant public ingress rules are:

```text
TCP 80
TCP 443
```

The previous public:

```text
TCP 8080
```

rule was removed.

No Docker container was stopped.

No application configuration was changed.

No NGINX configuration was changed.

The change was made only at the Oracle Cloud network-security layer.

---

## 7. Verification after the change

The public Spring Boot port was tested again from the local computer:

```bash
curl -I --connect-timeout 5 http://158.180.19.25:8080
```

Result:

```text
curl: (28) Failed to connect to 158.180.19.25 port 8080 after 5002 ms: Timeout was reached
```

This confirms that direct Internet access to port 8080 is now blocked.

---

## 8. Public HTTPS access still works

The normal public application URL was tested:

```bash
curl -I https://insuranceapp.gloziksoft.sk/
```

Result:

```text
HTTP/2 200
server: nginx/1.24.0 (Ubuntu)
```

Therefore the application remains publicly available through the intended HTTPS entry point.

The traffic path is now:

```text
Internet
    |
    | HTTPS :443
    v
Oracle Cloud
    |
    v
NGINX
    |
    | local proxy
    v
127.0.0.1:8080
    |
    v
Insurance Spring Boot
```

Direct:

```text
Internet
    |
    | :8080
    X
```

is blocked.

---

## 9. phpMyAdmin and cAdvisor

The direct public ports are also not used as the normal external access path:

```text
158.180.19.25:8081  -> timeout
158.180.19.25:8082  -> timeout
```

They remain available locally on the VM because Docker still publishes them there.

Important distinction:

Closing Oracle Cloud public ingress does **not** remove the Docker services.

It only removes their direct Internet access.

For example:

```text
127.0.0.1:8082
       |
       v
     cAdvisor
```

can still work on the VM even though:

```text
Internet -> 158.180.19.25:8082
```

is blocked.

---

## 10. Important distinction: port binding vs. public access

Docker may still publish ports such as:

```text
0.0.0.0:8080
0.0.0.0:8081
0.0.0.0:8082
```

This means Docker publishes the ports on the VM.

It does **not** by itself mean that the ports must be reachable from the Internet.

Public accessibility depends on the network path and security controls.

In this environment:

```text
Docker published port
        |
        v
Insurance VM
        |
        v
Oracle Cloud Security List / NSG
        |
        X
Internet access blocked
```

while NGINX remains the intended public entry point:

```text
Internet
    |
   443
    |
  NGINX
    |
    +----> application
    +----> management UI (current configuration)
```

---

## 11. Current network-security state

### Publicly exposed entry points

```text
80   -> NGINX / HTTP
443  -> NGINX / HTTPS
22   -> SSH
```

### Direct application/container ports

```text
8080 -> blocked from Internet
8081 -> blocked from Internet
8082 -> blocked from Internet
```

### Current architecture

```text
                         INTERNET
                             |
                     +-------+-------+
                     |               |
                    :80             :443
                     |               |
                     +-------+-------+
                             |
                           NGINX
                             |
              +--------------+--------------+
              |              |              |
             /               |        /phpmyadmin/
              |              |              |
              v              v              v
       Insurance App     other routes     phpMyAdmin
          :8080             ...             :8081


Direct Internet access:

158.180.19.25:8080  -> BLOCKED
158.180.19.25:8081  -> BLOCKED
158.180.19.25:8082  -> BLOCKED
```

---

## 12. What was learned

This change demonstrates an important distinction between several layers.

### Docker

Publishes a container port on the VM.

### Oracle Security List

Controls network traffic allowed through the subnet security list.

### Network Security Group (NSG)

Provides another Oracle Cloud network-security layer that can allow or block traffic for associated VNICs.

### NGINX

Acts as the intended public reverse proxy.

Therefore the goal is not necessarily to make every internal service stop listening.

The goal is to ensure that the Internet can reach only the intended public entry points.

---

## 13. Relation to Jiří's Point 2

Jiří recommended restricting the Oracle Cloud security configuration so that internal application/container ports are not directly exposed to the Internet.

For the Insurance VM this was verified and corrected.

### Before

```text
Internet
   |
   +---- :8080 ----> Spring Boot     PUBLIC
   |
   +---- :8081 ----> phpMyAdmin      not reachable externally
   |
   +---- :8082 ----> cAdvisor        not reachable externally
   |
   +---- :443 -----> NGINX
```

### After

```text
Internet
   |
   +---- :8080 ----> BLOCKED
   |
   +---- :8081 ----> BLOCKED
   |
   +---- :8082 ----> BLOCKED
   |
   +---- :443 -----> NGINX ----> internal services
```

**Point 2 – Insurance: completed and verified on 2026-09-27.**

---

## 14. Important follow-up – Jiří's Point 3

This document covers the network-level hardening from Point 2.

It does **not** mean that all management interfaces are now completely private.

For example, the current NGINX configuration still exposes:

```text
https://insuranceapp.gloziksoft.sk/phpmyadmin/
```

through HTTPS/443.

Therefore Point 3 must separately address:

> How should phpMyAdmin and other management/monitoring interfaces be accessed in a production-like environment?

Possible approaches discussed for that later step include:

- SSH tunneling
- VPN
- strict IP allowlisting

No such additional change is included in Point 2.

---

## 15. Access model after Point 2

### Application

Use:

```text
https://insuranceapp.gloziksoft.sk/
```

### Direct Spring Boot port

```text
http://158.180.19.25:8080
```

is no longer an external access method.

### Direct phpMyAdmin port

```text
http://158.180.19.25:8081
```

is no longer an external access method.

### Direct cAdvisor port

```text
http://158.180.19.25:8082
```

is no longer an external access method.

The detailed management-access method will be documented after completing Point 3.

---

**Status:** Point 2 – Insurance completed, changed and externally verified.
