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
│   ├── BusCompanyController.java
│   └── TravelerController.java
│
├── dto/
│   ├── BusCompanyRequest.java
│   ├── BusCompanyResponse.java
│   ├── BusCompanyUpdateRequest.java
│   ├── BusCompanyUpdateResponse.java
│   ├── TravelerRequest.java
│   ├── TravelerResponse.java
│   ├── TravelerUpdateRequest.java
│   └── TravelerUpdateResponse.java
│
├── exception/
│   ├── GlobalExceptionHandler.java    # @RestControllerAdvice
│   └── HandlerConfig.java             # Base exception with HTTP status
│
├── model/
│   ├── BusCompany.java
│   └── Traveler.java                  # @Formula age, creditsBalance initialized in @PrePersist
│
├── repository/
│   ├── BusCompanyRepository.java
│   └── TravelerRepository.java
│
├── service/
│   ├── BusCompanyService.java
│   └── TravelerService.java
│
└── validation/
    ├── ValidCpf.java                  # Custom annotation
    └── CpfValidator.java              # Digit-verifier algorithm
```

---

## Endpoints

### Bus Company — `/companies`

| Method | Path | Description | Status |
|---|---|---|---|
| `POST` | `/companies` | Register a new bus company | `201 Created` |
| `GET` | `/companies/{id}` | Find company by ID | `200 OK` |
| `PUT` | `/companies/{id}` | Update company profile | `200 OK` |
| `DELETE` | `/companies/{id}` | Delete company | `204 No Content` |

### Traveler — `/travelers`

| Method | Path | Description | Status |
|---|---|---|---|
| `POST` | `/travelers` | Register a new traveler | `201 Created` |
| `GET` | `/travelers/{id}` | Find traveler by ID | `200 OK` |
| `PUT` | `/travelers/{id}` | Update traveler profile | `200 OK` |
| `DELETE` | `/travelers/{id}` | Delete traveler | `204 No Content` |

#### POST `/travelers` — Request body
```json
{
  "name": "João Silva",
  "birthDate": "1990-05-15",
  "cpf": "12345678909",
  "telephone": "21987654321",
  "email": "joao@email.com",
  "password": "senha123"
}
```

#### POST `/travelers` — Response `201`
```json
{
  "id": 1,
  "name": "João Silva",
  "cpf": "12345678909",
  "birthDate": "1990-05-15",
  "age": 35,
  "email": "joao@email.com",
  "telephone": "21987654321",
  "creditsBalance": 0.00,
  "createdAt": "2026-06-28T10:00:00"
}
```

---

## Validation

| Field | Rule |
|---|---|
| `cpf` | Custom `@ValidCpf` — validates length, all-same-digit rejection, and both verifier digits |
| `cnpj` | `@Pattern(regexp = "\\d{14}")` |
| `telephone` | `@Pattern(regexp = "\\d{10,11}")` |
| `email` | `@Email` |
| `password` | `@Size(min = 6, max = 16)` |
| `birthDate` | `@NotNull` + `@Past` |
| text fields | `@NotBlank` |

---

## Error Handling

All errors return a consistent JSON structure via `GlobalExceptionHandler`:

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
| Email or CNPJ/CPF already registered | `409 Conflict` |
| Validation failure (`@Valid`) | `400 Bad Request` |
| Invalid argument | `400 Bad Request` |

---

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+
- PostgreSQL 14+

### 1. Clone

```bash
git clone https://github.com/Emanoel-H/Java-Mastery.git
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
