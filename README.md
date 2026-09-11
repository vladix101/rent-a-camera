# Rent-A-Camera

A full-stack camera rental application. Clients browse the camera fleet, check availability on a
calendar, book a rental and receive a PDF confirmation by email. Employees manage the fleet, the
catalog and the client list, and see rental statistics.

Built as a Spring Boot REST API with a React single-page frontend and a MySQL database.

## Tech stack

| Layer    | Technology                                                        |
|----------|-------------------------------------------------------------------|
| Backend  | Java 17, Spring Boot 4, Spring Data JPA, Liquibase, JJWT, OpenPDF  |
| Frontend | React 19, Vite, React Router, React-Bootstrap, Recharts           |
| Database | MySQL 8                                                           |

## Features

**Clients**
- Register with email verification (a 6-digit code, valid for 10 minutes)
- Browse the camera fleet and filter it by rental period
- Search by manufacturer, category, resolution or description
- Inspect a camera's full specification and its availability calendar
- Book a rental through a payment form and get a PDF confirmation by email
- Review their own rentals

**Employees**
- Add, edit and remove cameras (a camera with existing rentals is retired, not deleted)
- Add categories and specifications to the catalog
- Browse, edit and delete client accounts
- See a statistics dashboard: totals, availability for a chosen period, most-rented camera, and a
  pie chart of rental share per camera

## Getting started

### Prerequisites

- JDK 17 or newer
- Node.js 18 or newer
- A running MySQL 8 server

### 1. Create the database

```sql
CREATE DATABASE njtfoto;
```

Tables and demo data are created automatically on first start (Hibernate creates the schema,
Liquibase seeds it).

### 2. Configure the backend

The backend reads its secrets from environment variables and falls back to local-development
defaults. Nothing is required for a plain local run against `root` with an empty password.

| Variable        | Purpose                                   | Default                    |
|-----------------|-------------------------------------------|----------------------------|
| `DB_USERNAME`   | MySQL user                                | `root`                     |
| `DB_PASSWORD`   | MySQL password                            | *(empty)*                  |
| `MAIL_USERNAME` | Gmail address used to send mail           | *(empty — mail disabled)*  |
| `MAIL_PASSWORD` | Gmail **app password**, not the real one  | *(empty — mail disabled)*  |
| `JWT_SECRET`    | Signing key for JWTs                      | a local-development value  |

Set `JWT_SECRET` to a real secret before deploying anywhere. Leaving the mail variables unset is
fine: registration and booking still work, only the verification and confirmation emails are not
delivered.

### 3. Run the backend

```bash
cd backend
./mvnw spring-boot:run          # Windows: mvnw.cmd spring-boot:run
```

The API listens on `http://localhost:8080`.

### 4. Run the frontend

```bash
cd frontend
npm install
npm run dev
```

The app is served on `http://localhost:5173`. It auto-detects the backend at
`<page protocol>//<page hostname>:8080`, so testing from a phone on the same Wi-Fi works without
configuration. To point it elsewhere, copy `frontend/.env.example` to `frontend/.env` and set
`VITE_API_BASE_URL`.

## Run with Docker

The whole stack — MySQL, the API and the frontend — runs from a single Compose file, so nothing
needs to be installed apart from Docker.

```bash
docker compose up --build
```

| Service  | URL / port                                  |
|----------|---------------------------------------------|
| Frontend | http://localhost:5174                       |
| Backend  | http://localhost:8081                       |
| MySQL    | `localhost:3308` (root / root, db `njtfoto`)|

The host ports are deliberately offset from the defaults so this stack can run alongside a local
MySQL on 3306 and alongside other projects. Database schema and demo data are created on first
start.

To send verification and confirmation emails, create a `.env` file next to `docker-compose.yml`
(it is git-ignored):

```
MAIL_USERNAME=your.address@gmail.com
MAIL_PASSWORD=your-gmail-app-password
JWT_SECRET=some-long-random-secret
```

Without it the app still runs; only the emails are not delivered.

## Demo accounts

Seeded by Liquibase, for local testing only.

| Role     | Username   | Password      |
|----------|------------|---------------|
| Client   | `client`   | `client123`   |
| Employee | `employee` | `employee123` |

## API overview

All endpoints live under `/api`. Endpoints marked with a role require an
`Authorization: Bearer <token>` header carrying a token of that role.

| Method | Endpoint                          | Role     | Purpose                          |
|--------|-----------------------------------|----------|----------------------------------|
| GET    | `/cameras?dateFrom=&dateTo=`      | —        | Fleet, with availability flags    |
| GET    | `/cameras/{id}/occupancy`         | —        | Booked periods for one camera     |
| POST   | `/cameras`                        | Employee | Add a camera                      |
| PUT    | `/cameras/{id}`                   | Employee | Edit a camera                     |
| DELETE | `/cameras/{id}`                   | Employee | Remove or retire a camera         |
| POST   | `/rentals`                        | Client   | Book a rental                     |
| GET    | `/rentals/my`                     | Client   | The signed-in client's rentals    |
| GET    | `/rentals/stats?dateFrom=&dateTo=`| Employee | Dashboard statistics              |
| POST   | `/clients/login`                  | —        | Client sign-in                    |
| POST   | `/clients/register`               | —        | Start registration, send the code |
| POST   | `/clients/register/verify`        | —        | Confirm the code, create account  |
| GET    | `/clients`                        | Employee | List clients                      |
| PUT    | `/clients/{id}`                   | Employee | Edit a client                     |
| DELETE | `/clients/{id}`                   | Employee | Delete a client                   |
| POST   | `/employees/login`                | —        | Employee sign-in                  |
| GET    | `/categories`, `/specifications`  | —        | Catalog lookups                   |
| POST   | `/categories`, `/specifications`  | Employee | Extend the catalog                |
| GET    | `/manufacturers`                  | —        | Manufacturer lookup               |

## Project structure

```
backend/
  src/main/java/com/projekat/backend/
    controller/   REST endpoints
    service/      business rules, validation, email, PDF generation
    repository/   Spring Data JPA repositories
    entity/       JPA entities (Camera, Rental, Client, Employee, ...)
    dto/          request and response payloads
    security/     JWT creation and verification
    exception/    validation errors and the global handler
  src/main/resources/db/changelog/   Liquibase changelog
frontend/
  src/pages/      one folder per screen
  src/api/        API base-URL resolution
  src/utils/      category-to-image mapping
  src/assets/     camera illustrations
```

## Notes

- Passwords are stored and compared in plain text, and the payment form does not talk to a real
  processor. This is a university project, not production software.
- The domain was originally written in Serbian. Liquibase changeset `005` migrates an existing
  Serbian database (`fotoaparat`, `klijent`, …) to the English schema (`camera`, `client`, …) and
  seeds a fresh one; changesets `001`–`004` are kept unchanged so existing checksums stay valid.
