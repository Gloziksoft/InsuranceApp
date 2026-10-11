# Insurance App – Password Reset & Production Troubleshooting

## 1. Missing password reset columns in production

### Problem

Password reset failed in production because the `user_entity` table
did not contain:

- `reset_token`
- `reset_token_expiry`

### Cause

The production database schema was older than the current application
model.

### Solution

A Flyway migration was added:

`V1__add_password_reset_columns.sql`

The migration adds:

```sql
ALTER TABLE user_entity
    ADD COLUMN reset_token VARCHAR(255) NULL,
    ADD COLUMN reset_token_expiry DATETIME NULL;
```

Production uses:

```properties
spring.jpa.hibernate.ddl-auto=none
```

Therefore database schema changes are managed through Flyway.

---

## 2. Password reset token handling

Password reset uses a UUID token with a 15-minute validity period.

After a successful password reset, the token is invalidated and cannot
be reused.

---

## 3. Production mail configuration

Password reset emails use the configured SMTP server.

The sender address is configured through:

```text
MAIL_FROM
```

The production application uses:

```properties
app.mail.from=${MAIL_FROM}
```

The actual password reset notification is generated successfully in
production.

---

## 4. phpMyAdmin and database access

The MariaDB database is not exposed directly to the Internet.

phpMyAdmin is bound only to:

```text
127.0.0.1:8081
```

Remote administration is performed through an SSH tunnel.

Example:

```bash
ssh -4 -N -L 127.0.0.1:18084:127.0.0.1:8081 ubuntu@<SERVER_IP>
```

phpMyAdmin can then be accessed locally through the forwarded port.

---

## 5. Monitoring access

cAdvisor is bound only to the private VM address and is not publicly
accessible.

The production VM exposes only the required public services.

---

## 6. Security configuration warnings

The deprecated `AntPathRequestMatcher` usage was removed from the
Spring Security configuration.

MapStruct warnings related to the `events` property were resolved by
explicitly ignoring the property where it is not mapped.

---

## 7. Final verification

The application was verified with:

```text
Tests run: 10
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS
```
