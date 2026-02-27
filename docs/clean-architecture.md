# Clean Architecture – Spring Boot Project Structure

## Overview

Clean Architecture (by Robert C. Martin) organizes code into concentric layers where **dependencies always point inward**. The inner layers know nothing about the outer layers.

```
┌──────────────────────────────────────────┐
│              Frameworks & Drivers         │  ← Infrastructure, Web, DB
│   ┌──────────────────────────────────┐   │
│   │        Interface Adapters         │   │  ← Controllers, Gateways, Presenters
│   │   ┌──────────────────────────┐   │   │
│   │   │      Application         │   │   │  ← Use Cases / Services
│   │   │  ┌────────────────────┐  │   │   │
│   │   │  │      Domain        │  │   │   │  ← Entities, Value Objects, Rules
│   │   │  └────────────────────┘  │   │   │
│   │   └──────────────────────────┘   │   │
│   └──────────────────────────────────┘   │
└──────────────────────────────────────────┘
```

---

## Recommended Folder Structure

```
src/
└── main/
    ├── java/
    │   └── com/yourcompany/yourapp/
    │       │
    │       ├── domain/                          # 🟡 DOMAIN LAYER (innermost)
    │       │   ├── entity/                      # Core business entities
    │       │   │   └── User.java
    │       │   ├── valueobject/                 # Immutable value types
    │       │   │   └── Email.java
    │       │   ├── exception/                   # Domain-specific exceptions
    │       │   │   └── UserNotFoundException.java
    │       │   └── event/                       # Domain events (optional)
    │       │       └── UserCreatedEvent.java
    │       │
    │       ├── application/                     # 🟠 APPLICATION LAYER
    │       │   ├── usecase/                     # One class per use case
    │       │   │   ├── CreateUserUseCase.java
    │       │   │   └── GetUserUseCase.java
    │       │   ├── port/                        # Interfaces (boundaries)
    │       │   │   ├── in/                      # Driving ports (input)
    │       │   │   │   └── CreateUserInputPort.java
    │       │   │   └── out/                     # Driven ports (output)
    │       │   │       └── UserRepositoryPort.java
    │       │   ├── dto/                         # Application-level DTOs
    │       │   │   ├── request/
    │       │   │   │   └── CreateUserRequest.java
    │       │   │   └── response/
    │       │   │       └── UserResponse.java
    │       │   └── mapper/                      # Domain ↔ DTO mappers
    │       │       └── UserMapper.java
    │       │
    │       ├── adapter/                         # 🔵 INTERFACE ADAPTER LAYER
    │       │   ├── in/                          # Driving adapters
    │       │   │   └── web/
    │       │   │       ├── UserController.java
    │       │   │       └── GlobalExceptionHandler.java
    │       │   └── out/                         # Driven adapters
    │       │       ├── persistence/
    │       │       │   ├── UserPersistenceAdapter.java
    │       │       │   ├── UserJpaRepository.java
    │       │       │   └── UserEntity.java      # JPA entity (not domain entity)
    │       │       └── messaging/               # e.g. Kafka, RabbitMQ
    │       │           └── UserEventPublisher.java
    │       │
    │       └── infrastructure/                  # 🔴 INFRASTRUCTURE LAYER (outermost)
    │           ├── config/                      # Spring configs & beans
    │           │   ├── SecurityConfig.java
    │           │   ├── SwaggerConfig.java
    │           │   └── PersistenceConfig.java
    │           └── external/                    # Third-party integrations
    │               └── EmailServiceClient.java
    │
    └── resources/
        ├── application.yml
        ├── application-dev.yml
        ├── application-prod.yml
        └── db/
            └── migration/                       # Flyway / Liquibase
                └── V1__create_users_table.sql
```

---

## Layer Responsibilities

### 🟡 Domain Layer
The heart of the application. Contains pure business logic with **zero dependencies** on frameworks or libraries.

| Component | Purpose |
|---|---|
| `entity/` | Core business objects with identity (e.g., `User`, `Order`) |
| `valueobject/` | Immutable descriptors without identity (e.g., `Email`, `Money`) |
| `exception/` | Business rule violations |
| `event/` | Things that happened in the domain |

> **Rule:** No Spring annotations, no JPA, no external imports here.

---

### 🟠 Application Layer
Orchestrates the flow of data and coordinates domain objects to fulfil use cases.

| Component | Purpose |
|---|---|
| `usecase/` | One class per business use case |
| `port/in/` | Input port interfaces (what the use case exposes) |
| `port/out/` | Output port interfaces (what the use case needs from outside) |
| `dto/` | Data structures crossing the application boundary |
| `mapper/` | Converts between domain objects and DTOs |

> **Rule:** Depends only on the Domain layer. No Spring Web, no JPA.

---

### 🔵 Interface Adapter Layer
Converts data from the format most convenient for use cases into the format most convenient for external agencies, and vice versa.

| Component | Purpose |
|---|---|
| `adapter/in/web/` | REST controllers — call input ports |
| `adapter/out/persistence/` | Implements output ports using JPA/DB |
| `adapter/out/messaging/` | Implements output ports using message brokers |

> **Rule:** Depends on Application layer ports. Adapters implement or use ports.

---

### 🔴 Infrastructure Layer
Wires everything together. Contains all framework-specific configuration.

| Component | Purpose |
|---|---|
| `config/` | Spring beans, security, Swagger, DB config |
| `external/` | HTTP clients, third-party SDKs |

---

## Dependency Rule Summary

```
Infrastructure → Adapter → Application → Domain
      (outer)                              (inner)
```

Inner layers **never** import from outer layers. Outer layers depend on inner layer **interfaces** (ports), not implementations.

---

## Example: Create User Flow

```
HTTP POST /users
    │
    ▼
UserController (adapter/in/web)
    │  calls CreateUserInputPort
    ▼
CreateUserUseCase (application/usecase)
    │  uses domain entity + calls UserRepositoryPort
    ▼
UserPersistenceAdapter (adapter/out/persistence)
    │  implements UserRepositoryPort
    ▼
UserJpaRepository (Spring Data JPA)
    │
    ▼
Database
```

---

## Key Principles

1. **Dependency Inversion** — Use cases define interfaces (ports); infrastructure implements them.
2. **Single Responsibility** — One use case per class.
3. **Separate JPA Entities from Domain Entities** — `UserEntity` (JPA) ≠ `User` (domain).
4. **DTOs at boundaries** — Don't leak domain objects into controllers or persistence.
5. **Testability** — Domain and application layers should be testable without Spring context.

---

## Recommended Dependencies (`pom.xml` / `build.gradle`)

```xml
<!-- Core -->
spring-boot-starter-web
spring-boot-starter-data-jpa
spring-boot-starter-validation

<!-- Mapping -->
mapstruct

<!-- Testing -->
spring-boot-starter-test
testcontainers

<!-- Optional -->
spring-boot-starter-security
springdoc-openapi-starter-webmvc-ui   <!-- Swagger UI -->
flyway-core
```

---

*Last updated: 2026 | Based on Robert C. Martin's Clean Architecture + Tom Hombergs' "Get Your Hands Dirty on Clean Architecture"*