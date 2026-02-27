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

## Actual Project Structure

```
src/
└── main/
    ├── java/
    │   └── com/afadhitya/taskmanagement/
    │       │
    │       ├── domain/                          # 🟡 DOMAIN LAYER (innermost)
    │       │   ├── entity/                      # Core business entities
    │       │   │   ├── User.java
    │       │   │   ├── Workspace.java
    │       │   │   ├── WorkspaceMember.java
    │       │   │   ├── Project.java
    │       │   │   ├── ProjectMember.java
    │       │   │   ├── Task.java
    │       │   │   ├── TaskLabel.java
    │       │   │   ├── Label.java
    │       │   │   ├── Comment.java
    │       │   │   ├── Attachment.java
    │       │   │   ├── Notification.java
    │       │   │   └── AuditLog.java
    │       │   └── enums/                       # Domain enums
    │       │       ├── PlanTier.java
    │       │       ├── WorkspaceRole.java
    │       │       ├── ProjectPermission.java
    │       │       ├── TaskStatus.java
    │       │       └── TaskPriority.java
    │       │
    │       ├── application/                     # 🟠 APPLICATION LAYER
    │       │   ├── usecase/                     # Use case implementations
    │       │   │   ├── auth/
    │       │   │   │   └── RegisterUseCaseImpl.java
    │       │   │   ├── user/
    │       │   │   │   ├── CreateUserUseCaseImpl.java
    │       │   │   │   ├── GetUserByIdUseCaseImpl.java
    │       │   │   │   ├── GetAllUsersUseCaseImpl.java
    │       │   │   │   ├── UpdateUserUseCaseImpl.java
    │       │   │   │   └── DeleteUserByIdUseCaseImpl.java
    │       │   │   └── workspace/
    │       │   │       ├── CreateWorkspaceUseCaseImpl.java
    │       │   │       ├── GetWorkspaceByIdUseCaseImpl.java
    │       │   │       ├── UpdateWorkspaceUseCaseImpl.java
    │       │   │       └── DeleteWorkspaceByIdUseCaseImpl.java
    │       │   ├── port/                        # Interfaces (boundaries)
    │       │   │   ├── in/                      # Driving ports (input)
    │       │   │   │   ├── auth/
    │       │   │   │   │   └── RegisterUseCase.java
    │       │   │   │   ├── user/
    │       │   │   │   │   ├── CreateUserUseCase.java
    │       │   │   │   │   ├── GetUserByIdUseCase.java
    │       │   │   │   │   ├── GetAllUsersUseCase.java
    │       │   │   │   │   ├── UpdateUserUseCase.java
    │       │   │   │   │   └── DeleteUserByIdUseCase.java
    │       │   │   │   └── workspace/
    │       │   │   │       ├── CreateWorkspaceUseCase.java
    │       │   │   │       ├── GetWorkspaceByIdUseCase.java
    │       │   │   │       ├── UpdateWorkspaceUseCase.java
    │       │   │   │       └── DeleteWorkspaceByIdUseCase.java
    │       │   │   └── out/                     # Driven ports (output)
    │       │   │       ├── auth/
    │       │   │       │   └── UserAuthPersistencePort.java
    │       │   │       ├── user/
    │       │   │       │   └── UserPersistencePort.java
    │       │   │       └── workspace/
    │       │   │           └── WorkspacePersistencePort.java
    │       │   ├── dto/                         # Application-level DTOs
    │       │   │   ├── request/
    │       │   │   │   ├── RegisterRequest.java
    │       │   │   │   ├── CreateUserRequest.java
    │       │   │   │   ├── UpdateUserRequest.java
    │       │   │   │   ├── CreateWorkspaceRequest.java
    │       │   │   │   └── UpdateWorkspaceRequest.java
    │       │   │   └── response/
    │       │   │       ├── AuthResponse.java
    │       │   │       ├── UserResponse.java
    │       │   │       └── WorkspaceResponse.java
    │       │   └── mapper/                      # MapStruct mappers
    │       │       ├── UserMapper.java
    │       │       └── WorkspaceMapper.java
    │       │
    │       ├── adapter/                         # 🔵 INTERFACE ADAPTER LAYER
    │       │   ├── in/                          # Driving adapters
    │       │   │   └── web/
    │       │   │       ├── AuthController.java       # /api/auth/*
    │       │   │       ├── UserController.java       # /api/users/*
    │       │   │       └── WorkspaceController.java  # /workspaces/*
    │       │   └── out/                         # Driven adapters
    │       │       └── persistence/
    │       │           ├── UserRepository.java
    │       │           ├── WorkspaceRepository.java
    │       │           ├── auth/
    │       │           │   └── UserAuthPersistenceAdapter.java
    │       │           ├── user/
    │       │           │   └── UserPersistenceAdapter.java
    │       │           └── workspace/
    │       │               └── WorkspacePersistenceAdapter.java
    │       │
    │       └── infrastructure/                  # 🔴 INFRASTRUCTURE LAYER (outermost)
    │           └── config/
    │               └── SecurityConfig.java      # Password encoder config
    │
    └── resources/
        ├── application.properties               # Main config
        └── db/
            └── migration/
                └── V1__Initial_schema.sql       # Flyway migration
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

## Actual Dependencies (build.gradle)

```gradle
// Core Spring Boot
spring-boot-starter (4.0.3)
spring-boot-starter-web
spring-boot-starter-data-jpa
spring-boot-starter-validation

// Database
flyway-core + flyway-database-postgresql
postgresql (runtime)

// Mapping
mapstruct 1.6.3
mapstruct-processor 1.6.3 (annotation processor)

// Utilities
lombok (compileOnly + annotationProcessor)

// Security
spring-security-crypto

// Testing
spring-boot-starter-test
```

## Current Implementation Notes

### ✅ Implemented
- **Domain Layer**: All entities and enums defined
- **Application Layer**: Use cases for Auth (Register), User (CRUD), Workspace (CRUD)
- **Adapter Layer**: Controllers and persistence adapters
- **Infrastructure**: SecurityConfig with BCrypt password encoder
- **Database**: Flyway migration (V1__Initial_schema.sql)

### 🔧 Architecture Decisions
1. **No separate JPA Entities** - Using JPA annotations directly on domain entities (pragmatic approach for MVP)
2. **MapStruct for mapping** - Between domain entities and DTOs
3. **Lombok** - For reducing boilerplate code (`@Builder`, `@Value`, `@RequiredArgsConstructor`)
4. **Immutable Objects** - Prefer `final` fields, use `@Value` or `@Builder` to avoid setters
5. **Builder Pattern** - Always use `@Builder` for constructing DTOs and entities
6. **No explicit output ports for repositories** - Spring Data JPA repositories used directly in adapters

### 📋 Pending Implementations
- Global exception handler
- JWT authentication filter
- Method-level security (@PreAuthorize)
- Remaining use cases (Projects, Tasks, Comments, Labels, Attachments, Notifications)
- OpenAPI/Swagger documentation

---

*Last updated: 2026 | Based on Robert C. Martin's Clean Architecture + Tom Hombergs' "Get Your Hands Dirty on Clean Architecture"*