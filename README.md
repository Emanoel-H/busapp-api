# 🚌 busapp-api

A RESTful API for bus ticket management built with Spring Boot. This project is the REST evolution of [BusApp](https://github.com/Emanoel-H/Java-Mastery), rebuilt as a production-grade API with JWT authentication, BCrypt password hashing, Bean Validation, custom validators, and centralized error handling.

---

## 📋 Table of Contents

- [About](#about)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Endpoints](#endpoints)
- [Authentication](#authentication)
- [Validation](#validation)
- [Error Handling](#error-handling)
- [Getting Started](#getting-started)
- [Testing](#testing)
- [Known Limitations](#known-limitations)

---

## About

busapp-api manages bus trips and tickets through a REST interface. Two user types interact with the system:

- **Bus Companies** — register, manage trips, update profile
- **Travelers** — browse trips, buy tickets, cancel with credit refund

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21+ |
| Framework | Spring Boot 4 |
| ORM | Spring Data JPA / Hibernate |
| Database | PostgreSQL |
| Security | Spring Security + JWT (jjwt) + BCrypt |
| Distance API | OSRM (real-route), Haversine fallback |
| Validation | Jakarta Bean Validation (`@Valid`, custom `@ValidCpf`) |
| Testing | JUnit 5, Mockito, AssertJ |
| Containers | Docker, Docker Compose |
| Build | Maven |
| Utilities | Lombok, Jackson |

---

## Project Structure

```
src/main/java/br/com/javamastery/busapp_api/
│
├── client/
│   ├── OsrmClient.java                # @Component: OSRM distance + Haversine fallback
│   └── dto/
│       ├── OsrmResponse.java
│       └── OsrmRoute.java
│
├── controller/
│   ├── AddressController.java
│   ├── AuthController.java            # POST /auth/login
│   ├── BusCompanyController.java
│   ├── BusTicketController.java
│   ├── TravelerController.java
│   └── TripController.java            # Includes GET /trips/suggested-price
│
├── dto/
│   ├── BusCompanyRequest/Response/UpdateRequest/UpdateResponse
│   ├── BusTicketRequest/Response/CanceledResponse
│   ├── CityResponse, StateResponse
│   ├── LoginRequest, LoginResponse
│   ├── TravelerRequest/Response/UpdateRequest/UpdateResponse/CreditsResponse
│   └── TripRequest/Response/UpdateRequest/UpdateResponse
│
├── exception/
│   ├── GlobalExceptionHandler.java    # @RestControllerAdvice, unified buildResponse
│   └── HandlerConfig.java
│
├── model/
│   ├── BusCompany.java                # Constructor receives pre-encoded password
│   ├── BusTicket.java                 # cancelTicket(), price locked at persist, auto code
│   ├── Category.java (enum)
│   ├── City.java
│   ├── State.java
│   ├── Traveler.java                  # Constructor receives pre-encoded password
│   └── Trip.java                      # Soft delete, OSRM distance, auto code, category
│
├── repository/
│   ├── BusCompanyRepository.java
│   ├── BusTicketRepository.java       # JOIN FETCH, active/all by traveler, existsByTripId
│   ├── CityRepository.java
│   ├── StateRepository.java
│   ├── TravelerRepository.java
│   └── TripRepository.java
│
├── security/
│   ├── JwtFilter.java                 # OncePerRequestFilter — reads Bearer token
│   ├── JwtService.java                # generateToken(), extractEmail/Role(), isTokenValid()
│   └── SecurityConfig.java            # BCryptPasswordEncoder @Bean, stateless filter chain
│
├── service/
│   ├── AuthService.java               # passwordEncoder.matches() for both user types
│   ├── BusCompanyService.java         # passwordEncoder.encode() on register
│   ├── BusTicketService.java
│   ├── TravelerService.java           # passwordEncoder.encode() on register
│   └── TripService.java               # OSRM, suggestPrice()
│
└── validation/
    ├── ValidCpf.java
    └── CpfValidator.java

src/test/java/br/com/javamastery/busapp_api/
│
├── BusappApiApplicationTests.java     # contextLoads (needs a running PostgreSQL)
├── security/
│   └── JwtServiceTest.java
├── service/
│   ├── AuthServiceTest.java
│   ├── BusCompanyServiceTest.java
│   ├── BusTicketServiceTest.java
│   ├── TravelerServiceTest.java
│   └── TripServiceTest.java
└── validation/
    └── CpfValidatorTest.java
```

---

## Endpoints

### Auth — `/auth`

| Method | Path | Auth | Status |
|---|---|---|---|
| `POST` | `/auth/login` | Public | `200 OK` |

#### POST `/auth/login`
```json
// Request
{ "email": "user@email.com", "password": "senha123" }

// Response
{ "token": "eyJ...", "email": "user@email.com", "role": "TRAVELER" }
```

### Address — `/address` (public)

| Method | Path | Description |
|---|---|---|
| `GET` | `/address/states` | List all states |
| `GET` | `/address/states/{uf}/cities` | List cities by UF |
| `GET` | `/address/cities?city=nome` | Search by name prefix |

### Bus Company — `/companies`

| Method | Path | Auth | Status |
|---|---|---|---|
| `POST` | `/companies` | Public | `201 Created` |
| `GET` | `/companies/{id}` | Required | `200 OK` |
| `PUT` | `/companies/{id}` | Required | `200 OK` |
| `DELETE` | `/companies/{id}` | Required | `204 No Content` |

### Traveler — `/travelers`

| Method | Path | Auth | Status |
|---|---|---|---|
| `POST` | `/travelers` | Public | `201 Created` |
| `GET` | `/travelers/{id}` | Required | `200 OK` |
| `PUT` | `/travelers/{id}` | Required | `200 OK` |
| `DELETE` | `/travelers/{id}` | Required | `204 No Content` |
| `PATCH` | `/travelers/{id}/credits` | Required | `200 OK` |

### Trip — `/trips`

| Method | Path | Auth | Status |
|---|---|---|---|
| `POST` | `/trips` | Required | `201 Created` |
| `GET` | `/trips` | Public | `200 OK` |
| `GET` | `/trips?originCode=X&destinationCode=Y` | Public | `200 OK` |
| `GET` | `/trips?companyId=X` | Public | `200 OK` |
| `GET` | `/trips/{code}` | Public | `200 OK` |
| `GET` | `/trips/suggested-price?originCode=X&destinationCode=Y` | Public | `200 OK` |
| `PUT` | `/trips/{code}` | Required | `200 OK` |
| `DELETE` | `/trips/{code}` | Required | `204 No Content` |

### Ticket — `/tickets`

| Method | Path | Auth | Status |
|---|---|---|---|
| `POST` | `/tickets` | Required | `201 Created` |
| `GET` | `/tickets/{code}` | Required | `200 OK` |
| `GET` | `/tickets?traveler_id=X` | Required | `200 OK` |
| `GET` | `/tickets?traveler_id=X&includeCanceled=true` | Required | `200 OK` |
| `PATCH` | `/tickets/{code}/cancel` | Required | `200 OK` |

---

## Authentication

All protected endpoints require:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

Tokens encode `email` and `role` (`TRAVELER` or `COMPANY`). Configure in `application.properties`:
```properties
jwt.secret=your-256-bit-secret-minimum-32-characters-long
jwt.expiration=86400000
```

---

## Business Rules

- Passwords are hashed with **BCrypt** on registration and verified with `passwordEncoder.matches()` on login
- Tickets can only be canceled up to **1 hour before departure**
- Cancellation refunds the full ticket price as **credits** to the traveler
- Trips with associated tickets **cannot be deactivated**
- Ticket price is **locked at purchase time** via `@PrePersist`
- Only **active trips** can be booked
- Distance calculated via **OSRM API** with **Haversine fallback**
- Suggested price = distance (km) × R$ 0.35

---

## Validation

| Field | Rule |
|---|---|
| `cpf` | `@ValidCpf` — accepts masked or plain input; length, all-same-digit rejection, both verifier digits. Stored as 11 digits |
| `cnpj` | `@Pattern(regexp = "\\d{14}")` |
| `telephone` | `@Pattern(regexp = "\\d{10,11}")` |
| `email` | `@Email` |
| `password` | `@Size(min = 6, max = 16)` |
| `birthDate` | `@NotNull` + `@Past` |
| `departureDate` | `@NotNull` + `@Future` |
| `price` | `@DecimalMin("0.01")` |

---

## Error Handling

```json
{
  "TimeStamp": "2026-07-10T10:00:00",
  "Status": 401,
  "Message": "Invalid email or password"
}
```

| Scenario | HTTP Status |
|---|---|
| Resource not found | `404 Not Found` |
| Duplicate email / CNPJ / CPF | `409 Conflict` |
| Trip has associated tickets | `409 Conflict` |
| Cancellation window expired | `400 Bad Request` |
| Ticket already canceled | `400 Bad Request` |
| Invalid credentials | `401 Unauthorized` |
| Validation failure | `400 Bad Request` |

---

## Getting Started

### Option A — Docker Compose (recommended)

**Prerequisites:** Docker Desktop (Compose v2).

1. Copy `.env.example` to `.env` and fill in the values (the `.env` file is git-ignored):

   | Variable | Meaning |
   |---|---|
   | `DB_NAME`, `DB_USER`, `DB_PASSWORD` | PostgreSQL database and credentials |
   | `JWT_SECRET` | HMAC secret, **at least 32 characters** |
   | `JWT_EXPIRATION` | token lifetime in milliseconds (for example `86400000`) |

2. Build and start everything:

   ```bash
   docker compose up --build
   ```

3. Check it:

   ```bash
   curl http://localhost:8080/address/states   # 27 states
   ```

The stack has two services:

| Service | What it does |
|---|---|
| `db` | PostgreSQL with a named volume (`pgdata`) and a `pg_isready` healthcheck. On the first run it executes the scripts in `docker/initdb/`, which create and seed the `states` and `cities` tables (IBGE data). The port is **not** published to the host. |
| `api` | Built from the multi-stage `Dockerfile` (Maven + JDK to build with `-DskipTests`, then a slim JRE image). Starts only when `db` is healthy (`depends_on` with `condition: service_healthy`). Receives the datasource URL (host `db`), credentials and JWT settings from environment variables, and runs with `TZ=America/Sao_Paulo`. |

Useful commands:

```bash
docker compose logs -f api   # follow the API logs
docker compose down          # stop and remove containers, KEEPS the database volume
docker compose down -v       # also deletes the volume: empty database, seed runs again
```

The seed scripts only run when the data volume is empty.

### Option B — Run locally (without Docker)

#### Prerequisites

- Java 21+, Maven 3.8+, PostgreSQL 14+

#### Configure `application.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/busapp
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

jwt.secret=your-256-bit-secret-minimum-32-characters-long
jwt.expiration=86400000
```

#### Run

```bash
mvn spring-boot:run
```

API available at `http://localhost:8080`.

---

## Testing

Unit tests cover the service layer, where the business rules live, plus the pure-logic classes (`JwtService`, `CpfValidator`). Repositories, the `PasswordEncoder` and the `OsrmClient` are mocked with Mockito, so the tests run in milliseconds and do not need a database or network.

```bash
mvn test
```

| Test class | Tests | What it covers |
|---|---|---|
| `AuthServiceTest` | 4 | company login, traveler login, unknown email, wrong password (no token issued) |
| `BusCompanyServiceTest` | 7 | email/CNPJ conflicts, register (encode + save), update, delete, not found |
| `BusTicketServiceTest` | 8 | buy, cancel (already canceled, 1-hour window, credit refund and accumulation), lookups |
| `TravelerServiceTest` | 11 | email/CPF conflicts (including masked CPF), register, find, delete, `addCredits` rules, CPF stored without mask |
| `TripServiceTest` | 13 | create with OSRM distance, update, soft delete blocked by tickets, list filters, `suggestPrice` |
| `JwtServiceTest` | 6 | token round trip, expired token, wrong secret, garbage input |
| `CpfValidatorTest` | 9 | valid and masked CPFs, wrong check digit, repeated digits, wrong length, non-numeric, `null` |
| `BusappApiApplicationTests` | 1 | Spring context loads (requires PostgreSQL running locally) |

**Total: 59 tests passing.**

### Conventions

- `given / when / then` structure and descriptive `@DisplayName`.
- Failure tests assert both the `HttpStatus` and the message, so a test cannot pass for the wrong reason (an unstubbed `Optional` returns empty and would also give a 404).
- `verify(..., never()).save(...)` on every rejected path, to prove nothing was persisted.
- `City` and `State` have no setters, so fixtures are built with `ReflectionTestUtils.setField`.
- `ArgumentCaptor` checks what the service actually put inside the saved entity (for example, the distance coming from OSRM).
- Pure-logic classes (`CpfValidator`, `JwtService`) are tested without Mockito; `@ParameterizedTest` covers many inputs in a few lines.

### Known limits

- Mocked repositories do not execute the JPQL. A query bug (such as a missing `WHERE`) is invisible to these tests. Integration tests with Testcontainers are on the roadmap.
- `BusappApiApplicationTests` depends on a real database and on `jwt.secret` being configured.

---

## Known Limitations

- **Traveler `age` in create/update responses.** `age` is computed by the database (`@Formula`) when a row is read. `POST /travelers` and `PUT /travelers/{id}` return the entity that was just saved, so their response shows a stale `age` (`0` right after creation). `GET` returns the correct value. The formula also uses PostgreSQL-specific SQL.
- **Update endpoints do not check duplicates.** `PUT /travelers/{id}` and `PUT /companies/{id}` do not verify whether the new CPF or CNPJ already belongs to someone else, so a clash surfaces as a database error (500) instead of `409`. Registration does check.
- **Email is unique per table, not across them.** The same email can exist as a company and as a traveler. Login tries the company first.
- **The cancellation window uses the server clock.** `departureDate` and `departureTime` are entered in local (Brasília) time, so the server timezone must match. The compose file sets `TZ=America/Sao_Paulo`; running the jar elsewhere requires the same.
- **Unit tests do not run JPQL or hit a database**, and `BusappApiApplicationTests` needs a running PostgreSQL. Integration tests are on the roadmap.

---

## Author

Developed by [Emanoel H](https://github.com/Emanoel-H) as part of a Java learning journey focused on Spring Boot, REST API design, Spring Security, and professional software architecture.
