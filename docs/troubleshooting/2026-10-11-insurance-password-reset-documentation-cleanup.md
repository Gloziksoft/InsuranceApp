# 2026-10-11 — InsuranceApp password reset, documentation and repository cleanup

## Changes

- Completed the password reset implementation and verification in production.
- Added the Flyway migration for `reset_token` and `reset_token_expiry`.
- Fixed and verified the production mail configuration.
- Verified the password reset flow in production.
- Ran the complete test suite:
  - `mvn clean test`
  - 10 tests
  - 0 failures
  - 0 errors
- Completed final `README.md` and repository cleanup.
- Removed sensitive information from the public documentation.
- Created a production database backup before the final changes and kept it outside Git tracking.

## Result

InsuranceApp password reset, database migration, production mail configuration, tests, documentation and repository cleanup were completed successfully.
