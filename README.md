# Fingoal Backend (Spring Boot)

Replaces the FastAPI/Mongo scaffold from the original Fingoal AI project with a
proper Spring Boot + PostgreSQL + JWT backend that the React Native app talks to
over HTTPS instead of AsyncStorage/Supabase.

## Stack
- Java 21, Spring Boot 3.3
- PostgreSQL (Spring Data JPA / Hibernate)
- Spring Security + JWT (access + refresh tokens, self-issued — no Keycloak dependency)
- Bean Validation for request DTOs

## Modules
- `user` — registration, login, refresh, profile (onboarding fields like monthly income, EMI, etc.)
- `transaction` — CRUD for income/expense transactions
- `goal` — savings goals with on-track/behind calculation
- `dashboard` — aggregates this-month transactions into the Home tab summary
  (income/spent/saved, top category breakdown, recent transactions)
- `security` / `config` — JWT filter + Spring Security config
- `common` — shared API exception + global error handler

## Running locally

1. Start Postgres and create a database:
   ```sql
   CREATE DATABASE fingoal;
   CREATE USER fingoal WITH PASSWORD 'fingoal';
   GRANT ALL PRIVILEGES ON DATABASE fingoal TO fingoal;
   ```
2. Set a real JWT secret (don't use the default in `application.yml` outside local dev):
   ```bash
   export JWT_SECRET="$(openssl rand -base64 48)"
   ```
3. Run:
   ```bash
   mvn spring-boot:run
   ```
   Server starts on `http://localhost:8080`.

## API summary

| Method | Path                     | Auth | Description |
|--------|--------------------------|------|--------------|
| POST   | /api/auth/register       | no   | Create account, returns tokens |
| POST   | /api/auth/login          | no   | Returns access + refresh tokens |
| POST   | /api/auth/refresh        | no   | Exchange refresh token for a new pair |
| GET    | /api/auth/me             | yes  | Current user profile |
| PATCH  | /api/auth/me             | yes  | Update onboarding/profile fields |
| GET    | /api/transactions        | yes  | List transactions, newest first |
| POST   | /api/transactions        | yes  | Create a transaction |
| DELETE | /api/transactions/{id}   | yes  | Delete a transaction |
| GET    | /api/goals               | yes  | List goals |
| POST   | /api/goals               | yes  | Create a goal |
| DELETE | /api/goals/{id}          | yes  | Delete a goal |
| GET    | /api/dashboard/summary   | yes  | Home tab aggregate data |

All authenticated endpoints expect `Authorization: Bearer <accessToken>`.

## Not included in this slice (next steps)
- AI Advisor endpoint (proxy to an LLM with the user's financial summary as context)
- Alerts (subscription renewal, spending spike, low emergency fund) — these were
  client-side derived logic in the original app; worth moving server-side so both
  platforms see the same alerts
- Refresh token revocation / rotation (currently any valid refresh token can mint
  new tokens indefinitely until expiry — fine for MVP, not for production)
- Rate limiting on `/api/auth/*`
- Flyway/Liquibase migrations (currently relying on `ddl-auto: update`, fine for
  dev, replace before production)
