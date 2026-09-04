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
```
This removes unused dangling Docker images created by previous deployments
and prevents old application image layers from unnecessarily consuming disk space.

Insurance production test:

Before cleanup:

Docker images: 39
Image storage: 8.736 GB
Reclaimable: 2.429 GB
VM disk usage: ~45%

After automatic cleanup:

Docker images: 6
Image storage: 2.245 GB
Reclaimable: 1.344 GB
VM disk usage: ~30%

The cleanup removed old unused Docker images while keeping all active
application, database, and cAdvisor containers running.

The cleanup is now part of the automatic GitHub Actions production
deployment workflow and runs after the application deployment.

=========================================================================
