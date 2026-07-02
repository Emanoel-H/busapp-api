# 🚌 busapp-api

A RESTful API for bus ticket management built with Spring Boot. This project is the REST evolution of [BusApp](https://github.com/Emanoel-H/Java-Mastery), rebuilt as a production-grade API with proper layered architecture, Bean Validation, custom validators, and centralized error handling.

---

## 📋 Table of Contents

- [About](#about)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Endpoints](#endpoints)
- [Validation](#validation)
- [Error Handling](#error-handling)
- [Getting Started](#getting-started)
- [Roadmap](#roadmap)

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
| Validation | Jakarta Bean Validation (`@Valid`, custom `@ValidCpf`) |
| Build | Maven |
| Utilities | Lombok |

---

## Project Structure

```
src/main/java/br/com/javamastery/busapp_api/
│
├── controller/
│   ├── AddressController.java         # Read-only: states, cities by UF, city search
│   ├── BusCompanyController.java
│   ├── BusTicketController.java
│   ├── TravelerController.java
│   └── TripController.java
│
├── dto/
│   ├── BusCompanyRequest/Response/UpdateRequest/UpdateResponse
│   ├── BusTicketRequest/Response/CanceledResponse
│   ├── CityResponse, StateResponse
│   ├── TravelerRequest/Response/UpdateRequest/UpdateResponse/CreditsResponse
│   └── TripRequest/Response/UpdateRequest/UpdateResponse
│
├── exception/
│   ├── GlobalExceptionHandler.java    # @RestControllerAdvice, unified buildResponse
│   └── HandlerConfig.java             # Base exception with HTTP status
│
├── model/
│   ├── BusCompany.java
│   ├── BusTicket.java                 # cancelTicket(), price copied at persist, auto code
│   ├── Category.java (enum)
│   ├── City.java                      # Read-only, FetchType.LAZY
│   ├── State.java                     # Read-only
│   ├── Traveler.java                  # @Formula age, creditsBalance, addCredits()
│   └── Trip.java                      # Soft delete, Haversine, auto code, category
│
├── repository/
│   ├── BusCompanyRepository.java
│   ├── BusTicketRepository.java       # JOIN FETCH, findByCode, findAllByTravelerId, existsByTripId
│   ├── CityRepository.java            # JOIN FETCH queries, search by name/UF/IBGE
│   ├── StateRepository.java
│   ├── TravelerRepository.java
│   └── TripRepository.java            # JOIN FETCH, filter by route/company/active
│
├── service/
│   ├── BusCompanyService.java
│   ├── BusTicketService.java          # buy(), findByCode(), listAllByTraveler(), cancelTicket()
│   ├── TravelerService.java
│   └── TripService.java               # existsByTripId guard on delete
│
└── validation/
    ├── ValidCpf.java
    └── CpfValidator.java
```

---

## Endpoints

### Address — `/address` (read-only)

| Method | Path | Description |
|---|---|---|
| `GET` | `/address/states` | List all states |
| `GET` | `/address/states/{uf}/cities` | List cities by state UF |
| `GET` | `/address/cities?city=nome` | Search cities by name prefix |

### Bus Company — `/companies`

| Method | Path | Description | Status |
|---|---|---|---|
| `POST` | `/companies` | Register | `201 Created` |
| `GET` | `/companies/{id}` | Find by ID | `200 OK` |
| `PUT` | `/companies/{id}` | Update | `200 OK` |
| `DELETE` | `/companies/{id}` | Delete | `204 No Content` |

### Traveler — `/travelers`

| Method | Path | Description | Status |
|---|---|---|---|
| `POST` | `/travelers` | Register | `201 Created` |
| `GET` | `/travelers/{id}` | Find by ID | `200 OK` |
| `PUT` | `/travelers/{id}` | Update | `200 OK` |
| `DELETE` | `/travelers/{id}` | Delete | `204 No Content` |
| `PATCH` | `/travelers/{id}/credits` | Add credits | `200 OK` |

### Trip — `/trips`

| Method | Path | Description | Status |
|---|---|---|---|
| `POST` | `/trips` | Create trip | `201 Created` |
| `GET` | `/trips` | List all active | `200 OK` |
| `GET` | `/trips?originCode=X&destinationCode=Y` | Filter by route | `200 OK` |
| `GET` | `/trips?companyId=X` | Filter by company | `200 OK` |
| `GET` | `/trips/{code}` | Find by code | `200 OK` |
| `PUT` | `/trips/{code}` | Update | `200 OK` |
| `DELETE` | `/trips/{code}` | Soft delete | `204 No Content` |

### Ticket — `/tickets`

| Method | Path | Description | Status |
|---|---|---|---|
| `POST` | `/tickets` | Buy ticket | `201 Created` |
| `GET` | `/tickets/{code}` | Find by code | `200 OK` |
| `GET` | `/tickets?traveler_id=X` | List by traveler | `200 OK` |
| `GET` | `/tickets?traveler_id=X&includeCanceled=true` | Include canceled | `200 OK` |
| `PATCH` | `/tickets/{code}/cancel` | Cancel ticket | `200 OK` |

#### POST `/tickets` — Request body
```json
{
  "traveler_id": 1,
  "trip_code": "ABC1234567",
  "departureDate": "2026-08-15"
}
```

#### PATCH `/tickets/{code}/cancel` — Response `200`
```json
{
  "code": "XYZ9876543",
  "price": 89.90,
  "departureDate": "2026-08-15",
  "travelerName": "João Silva",
  "travelerCreditsBalance": 89.90,
  "cpf": "123.456.789-09",
  "originCity": "Rio de Janeiro",
  "originState": "Rio de Janeiro",
  "destinationCity": "São Paulo",
  "destinationState": "São Paulo",
  "cancelDate": "2026-08-14T10:00:00",
  "canceled": true
}
```

---

## Business Rules

- Tickets can only be canceled up to **1 hour before departure**
- Cancellation refunds the full ticket price as **credits** to the traveler
- Trips with associated tickets **cannot be deactivated**
- Ticket price is **locked at purchase time** (copied from the trip's current price)
- Only **active trips** can be booked

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
| text fields | `@NotBlank` |

---

## Error Handling

All errors return a consistent JSON structure:

```json
{
  "TimeStamp": "2026-06-28T10:00:00",
  "Status": 404,
  "Message": "TRAVELER NOT FOUND"
}
```

| Scenario | HTTP Status |
|---|---|
| Resource not found | `404 Not Found` |
| Duplicate email / CNPJ / CPF | `409 Conflict` |
| Trip has associated tickets | `409 Conflict` |
| Cancellation window expired | `400 Bad Request` |
| Ticket already canceled | `400 Bad Request` |
| Trip inactive | `400 Bad Request` |
| Validation failure | `400 Bad Request` |

---

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+
- PostgreSQL 14+

### 1. Clone

```bash
git clone https://github.com/Emanoel-H/busapp-api.git
cd busapp-api
```

### 2. Configure `application.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/busapp
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

### 3. Run

```bash
mvn spring-boot:run
```

API available at `http://localhost:8080`.

---

## Author

Developed by [Emanoel H](https://github.com/Emanoel-H) as part of a Java learning journey focused on Spring Boot, REST API design, JPA/Hibernate, and professional software architecture.
