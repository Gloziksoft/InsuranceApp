# 🚀 InsuranceApp – Run Guide

This guide explains how to run the InsuranceApp locally from a fresh repository checkout.

## 1. Prerequisites

Install and verify:

- Java 17+
- Maven
- Docker
- Docker Compose

Check the installed versions:

```bash
java -version
mvn -version
docker --version
docker compose version
```

---

## 2. Clone the repository

```bash
git clone <REPOSITORY_URL>
cd InsuranceApp
```

---

## 3. Configure local environment

Copy the example environment file:

```bash
cp .env.example .env
```

Replace the `CHANGE_ME` values in `.env` with your own settings.

The `.env` file is local only and must not be committed to the repository.

---

## 4. Start the local MariaDB database

The project provides a local Docker Compose configuration.

Start the database:

```bash
docker compose -f docker-compose.local.yml up -d
```

The local Spring profile connects to MariaDB on:

```text
localhost:3307
```

The database name is:

```text
insurance_app
```

Check the running containers:

```bash
docker ps
```

---

## 5. Run the application

The application uses the `local` Spring profile for local development.

Start it with Maven:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

Alternatively, open the project in IntelliJ IDEA and run:

```text
InsuranceAppApplication.java
```

---

## 6. Open the application

The default local application port is:

```text
8083
```

Open:

```text
http://localhost:8083
```

---

## 7. Create a user

For a fresh local environment, use the registration page to create a user:

```text
http://localhost:8083/account/register
```

No test passwords or production credentials are required.

---

## 8. Run the tests

Before making changes or submitting a pull request, run:

```bash
mvn clean test
```

---

## 9. Stop the local database

When finished:

```bash
docker compose -f docker-compose.local.yml down
```

To stop the containers without removing them:

```bash
docker compose -f docker-compose.local.yml stop
```

---

## 10. Troubleshooting

Production and development troubleshooting notes are stored in:

```text
docs/troubleshooting/
```

These documents contain the actual problems encountered during development and deployment and the solutions that were applied.
