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
| Framework | Spring Boot 3 |
| ORM | Spring Data JPA / Hibernate |
| Database | PostgreSQL |
| Security | Spring Security + JWT (jjwt) + BCrypt |
| Distance API | OSRM (real-route), Haversine fallback |
| Validation | Jakarta Bean Validation (`@Valid`, custom `@ValidCpf`) |
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
| `cpf` | `@ValidCpf` — length, all-same-digit rejection, both verifier digits |
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

### Prerequisites

- Java 21+, Maven 3.8+, PostgreSQL 14+

### Configure `application.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/busapp
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

jwt.secret=your-256-bit-secret-minimum-32-characters-long
jwt.expiration=86400000
```

### Run

```bash
mvn spring-boot:run
```

API available at `http://localhost:8080`.

---

## Author

Developed by [Emanoel H](https://github.com/Emanoel-H) as part of a Java learning journey focused on Spring Boot, REST API design, Spring Security, and professional software architecture.
