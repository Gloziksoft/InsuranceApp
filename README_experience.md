### 2026-08-23 — Insurance App: Deploy Only Application Container

Updated the Insurance App GitHub Actions deployment workflow to recreate
only the application container during a normal application deployment.

Changed the deployment command from:

```bash
docker compose up -d --force-recreate --remove-orphans

to:

docker compose up -d --force-recreate --no-deps app

This prevents unnecessary recreation of dependent services during an
application deployment.

Production deployment behavior:

insuranceapp-app-1 → recreated with the new application image
insuranceapp-db-1 → remains running
insuranceapp-phpmyadmin-1 → remains running
insuranceapp-cadvisor-1 → remains running
VM → remains running

This keeps application deployment isolated from database, monitoring,
and other infrastructure services and reduces unnecessary downtime.
```
### 2026-09-02 — Booking & Insurance App: Automatic Docker Image Cleanup

Added automatic Docker image cleanup to the production deployment workflows.

After a successful application deployment, the VM now runs:

```bash
docker image prune -f
