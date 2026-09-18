# Parking Lot Management

A backend application for managing parking operations across venues, parking areas, lots, slots, reservations, and payments. The system provides secure user authentication, persistent PostgreSQL storage, and containerized local development using Docker.

## Features

* Venue and parking area management
* Parking lot and slot management
* Real-time parking slot availability tracking
* Reservation management
* Payment processing support
* User authentication and authorization
* JWT-based security
* Scheduled application operations
* PostgreSQL database persistence
* Docker and Docker Compose support
* Layered backend architecture for maintainability and scalability

## Tech Stack

* **Java 17**
* **Spring Boot**
* **Spring Data JPA**
* **PostgreSQL**
* **Spring Security**
* **JWT (JJWT)**
* **Maven**
* **Docker**
* **Docker Compose**

## Architecture

The application follows a layered architecture that separates API handling, business logic, data access, and security concerns.

```text
                    Client
                      │
                      ▼
              ┌───────────────┐
              │   Controller  │
              └───────┬───────┘
                      │
                      ▼
              ┌───────────────┐
              │    Service    │
              │ Business Logic│
              └───────┬───────┘
                      │
                      ▼
              ┌───────────────┐
              │   Repository  │
              └───────┬───────┘
                      │
                      ▼
              ┌───────────────┐
              │  PostgreSQL   │
              └───────────────┘

              Security Layer
                    │
                    ▼
             JWT Authentication
```

## Project Structure

```text
Parking-Lot-Management/
├── .mvn/
├── src/
│   ├── main/
│   │   ├── java/com/parkinglot/parkinglot/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── exception/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   ├── security/
│   │   │   ├── service/
│   │   │   └── ParkinglotApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .gitignore
└── README.md
```

## Domain Model

The system is organized around several core entities:

```text
User
 │
 └── Role

Venue
 │
 └── Parking Area
       │
       └── Parking Lot
             │
             └── Slots
                   │
                   └── Reservations
                         │
                         └── Payments
```

### Core Entities

* **User** — Represents users interacting with the parking system.
* **Role** — Defines user permissions and access levels.
* **Venue** — Represents a location offering parking facilities.
* **Parking Area** — Organizes parking facilities within a venue.
* **Parking Lot** — Represents an individual parking facility.
* **Slot** — Represents an available parking space.
* **Reservation** — Handles booking and reservation information.
* **Payment** — Stores payment-related information associated with reservations.

## Prerequisites

Before running the project, make sure you have:

* Java 17 or later
* Maven
* PostgreSQL

For containerized development:

* Docker
* Docker Compose

## Configuration

Application configuration is stored in:

```text
src/main/resources/application.properties
```

The application supports environment variables for database and JWT configuration.

```properties
spring.application.name=parkinglot

spring.datasource.url=${SPRING_DATASOURCE_URL:jdbc:postgresql://localhost:5432/parking_db}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

jwt.secret=${JWT_SECRET}
jwt.expiration=${JWT_EXPIRATION:86400000}
```

### Environment Variables

| Variable                     | Required | Description                                                 |
| ---------------------------- | -------- | ----------------------------------------------------------- |
| `JWT_SECRET`                 | **Yes**  | Secret used for JWT generation and validation               |
| `JWT_EXPIRATION`             | No       | JWT expiration time in milliseconds; defaults to `86400000` |
| `SPRING_DATASOURCE_URL`      | No       | PostgreSQL connection URL; defaults to local `parking_db`   |
| `SPRING_DATASOURCE_USERNAME` | **Yes**  | PostgreSQL username for the local database                  |
| `SPRING_DATASOURCE_PASSWORD` | **Yes**  | PostgreSQL password for the local database                  |


> **Security:** Never commit real passwords, JWT secrets, API keys, or other credentials to the repository.

## Running the Application

### Option 1 — Maven

Clone the repository:

```bash
git clone https://github.com/seerat-00/Parking-Lot-Management.git
cd Parking-Lot-Management
```

Build the application:

```bash
./mvnw clean install
```

Run the application:

```bash
./mvnw spring-boot:run
```

The application runs on:

```text
http://localhost:8080
```

### Option 2 — Docker Compose

Start the complete application stack:

```bash
docker compose up --build
```

This starts the required services, including:

* PostgreSQL
* Spring Boot application

The services use:

```text
Application → 8080
PostgreSQL  → 5432
```

To stop the containers:

```bash
docker compose down
```

## Security

The application uses **Spring Security and JWT-based authentication** to protect application resources.

The security implementation includes:

* `JwtAuthFilter`
* `JwtUtil`
* `SecurityConfig`

The authentication flow can be summarized as:

```text
Login Request
     │
     ▼
Authentication
     │
     ▼
JWT Generated
     │
     ▼
Client Sends JWT
     │
     ▼
JwtAuthFilter
     │
     ▼
Authorization
     │
     ▼
Protected API
```

## Application Flow

A typical parking reservation workflow follows this process:

```text
User
 │
 ▼
Select Venue
 │
 ▼
Select Parking Area
 │
 ▼
View Available Slots
 │
 ▼
Select Slot
 │
 ▼
Create Reservation
 │
 ▼
Process Payment
 │
 ▼
Reservation Confirmed
```

This structure allows parking resources to be organized hierarchically while keeping reservation and payment operations connected to the selected parking slot.

## Backend Modules

### Controller

Responsible for exposing the application's REST endpoints and handling incoming requests.

### Service

Contains the core business logic and coordinates application operations.

### Repository

Provides database access through Spring Data JPA.

### Model

Contains the application's domain entities and database mappings.

### DTO

Defines request and response objects used when communicating with the API.

### Security

Handles authentication, authorization, JWT processing, and protected resources.

### Exception

Contains custom exception handling for application-level errors.

## Development

The project uses Maven for dependency management and builds.

Run tests with:

```bash
./mvnw test
```

Build without running tests:

```bash
./mvnw clean package -DskipTests
```

## Future Improvements

Potential areas for further development include:

* Redis caching for frequently accessed parking availability
* Kafka-based event processing
* Distributed locking for concurrent slot reservations
* Microservice decomposition for independent system components
* API documentation using OpenAPI/Swagger
* Monitoring with Prometheus and Grafana
* CI/CD automation
* Cloud deployment
* Automated integration and load testing

These are planned extension areas rather than features currently represented as implemented functionality.

## License

This project currently does not declare a license in the repository metadata.

## Author

**Seerat Sharma**

Software Engineering Student | Backend Developer

---

Built as a practical backend system to explore application architecture, secure APIs, database management, reservations, and scalable software design.
