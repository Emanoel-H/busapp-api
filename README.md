# 🚌 busapp-api

A RESTful API for bus ticket management built with Spring Boot. This project is the REST evolution of [BusApp](https://github.com/Emanoel-H/Java-Mastery), a console-based Java application, now rebuilt as a production-grade API with proper layered architecture, validation, and error handling.

---

## 📋 Table of Contents

- [About](#about)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Endpoints](#endpoints)
- [Error Handling](#error-handling)
- [Getting Started](#getting-started)
- [Environment Variables](#environment-variables)
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
| Validation | Jakarta Bean Validation (`@Valid`) |
| Build | Maven |
| Utilities | Lombok |

---

## Project Structure

```
src/main/java/br/com/javamastery/busapp_api/
│
├── controller/         # REST endpoints — delegates to service, no business logic
│   └── BusCompanyController.java
│
├── dto/                # Request and response objects
│   ├── BusCompanyRequest.java
│   ├── BusCompanyResponse.java
│   ├── BusCompanyUpdateRequest.java
│   └── BusCompanyUpdateResponse.java
│
├── exception/          # Global error handling
│   ├── GlobalExceptionHandler.java   # @RestControllerAdvice
│   └── HandlerConfig.java            # Base runtime exception with HTTP status
│
├── model/              # JPA Entities
│   └── BusCompany.java
│
├── repository/         # Spring Data JPA repositories
│   └── BusCompanyRepository.java
│
└── service/            # Business logic
    └── BusCompanyService.java
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

#### POST `/companies` — Request body
```json
{
  "legalName": "Transportes ABC Ltda",
  "tradingName": "ABC Bus",
  "cnpj": "12345678000195",
  "telephone": "21987654321",
  "email": "contato@abcbus.com.br",
  "password": "senha123"
}
```

#### POST `/companies` — Response `201`
```json
{
  "id": 1,
  "legalName": "Transportes ABC Ltda",
  "tradingName": "ABC Bus",
  "cnpj": "12345678000195",
  "telephone": "21987654321",
  "email": "contato@abcbus.com.br",
  "createdAt": "2026-06-27T10:00:00"
}
```

#### PUT `/companies/{id}` — Request body
```json
{
  "legalName": "Transportes ABC Ltda",
  "tradingName": "ABC Bus Novo Nome",
  "cnpj": "12345678000195",
  "telephone": "21912345678"
}
```

---

## Error Handling

All errors return a consistent JSON structure:

```json
{
  "TimeStamp": "2026-06-27T10:00:00",
  "Status": 404,
  "Message": "COMPANY NOT FOUND"
}
```

| Scenario | HTTP Status |
|---|---|
| Resource not found | `404 Not Found` |
| Email or CNPJ already registered | `409 Conflict` |
| Validation failure (`@Valid`) | `400 Bad Request` |
| Invalid argument | `400 Bad Request` |

---

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.8+
- PostgreSQL 14+

### 1. Clone the repository

```bash
git clone https://github.com/Emanoel-H/Java-Mastery.git
cd busapp-api
```

### 2. Set up the database

Create a PostgreSQL database named `busapp`.

### 3. Configure `application.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/busapp
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### 4. Run

```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`.

---

### In Progress
- [ ] Traveler module — register, findById, update, delete
- [ ] Trip module — create, search by origin/destination, update, delete

### Planned
- [ ] Authentication (Spring Security + JWT)
- [ ] Ticket module — buy, cancel, view
- [ ] OSRM integration for real-route distance and price suggestion
- [ ] Password hashing (BCrypt)
- [ ] Unit tests (JUnit 5 + Mockito)
- [ ] Docker
- [ ] MongoDB audit logging
- [ ] AWS deployment

---

## Author

Developed by [Emanoel H](https://github.com/Emanoel-H) as part of a Java learning journey focused on Spring Boot, REST API design, JPA, and professional software architecture.
