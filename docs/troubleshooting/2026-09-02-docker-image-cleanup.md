# InsuranceApp — Automatic Docker Image Cleanup

## Date

2026-09-02

---

## Problem

Old Docker images from previous deployments could remain on the
production VM and consume disk space.

---

## Solution

Automatic Docker image cleanup was added to the production deployment
workflow.

After a successful application deployment, the VM runs:

    docker image prune -f

This removes unused dangling Docker images created by previous
deployments.

---

## InsuranceApp Production Test

### Before Cleanup

    Docker images: 39
    Image storage: 8.736 GB
    Reclaimable: 2.429 GB
    VM disk usage: approximately 45%

### After Cleanup

    Docker images: 6
    Image storage: 2.245 GB
    Reclaimable: 1.344 GB
    VM disk usage: approximately 30%

The cleanup removed old unused Docker images while keeping the active
application, database and cAdvisor containers running.

---

## Deployment Workflow

The cleanup is executed after the application deployment as part of
the automatic GitHub Actions production deployment workflow.

---

## Lesson Learned

Automatic cleanup helps prevent old Docker image layers from
unnecessarily consuming disk space on a resource-constrained
production VM.
