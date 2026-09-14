# InsuranceApp — Deploy Only Application Container

## Date

2026-08-23

---

## Problem / Change

The InsuranceApp GitHub Actions deployment workflow was updated so that
a normal application deployment recreates only the application
container.

Previously the deployment used:

    docker compose up -d --force-recreate --remove-orphans

This could recreate dependent services unnecessarily.

---

## Solution

The deployment command was changed to:

    docker compose up -d --force-recreate --no-deps app

This keeps the application deployment isolated from dependent services.

---

## Production Deployment Behavior

During a normal application deployment:

    insuranceapp-app-1
        → recreated with the new application image

    insuranceapp-db-1
        → remains running

    insuranceapp-phpmyadmin-1
        → remains running

    insuranceapp-cadvisor-1
        → remains running

    VM
        → remains running

---

## Result

Application deployment is isolated from the database, monitoring and
other infrastructure services.

This reduces unnecessary service recreation and helps minimize
unnecessary downtime.

---

## Lesson Learned

A normal application deployment should affect only the components that
actually need to be updated.

Using:

    --no-deps

prevents Docker Compose from unnecessarily recreating dependent services.
