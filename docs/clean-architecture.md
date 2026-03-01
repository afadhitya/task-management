# Clean Architecture – Spring Boot Project Structure

**Related Documents:**
- [Feature Flag Technical Documentation](./feature-flag-technical.md) - Feature flag implementation details
- [Task Management App PRD](./task-management-app-prd-backend.md) - Product requirements

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
    │       │   ├── entity/                      # Core business entities (JPA annotated)
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
    │       │   ├── enums/                       # Domain enums
    │       │   │   ├── PlanTier.java
    │       │   │   ├── WorkspaceRole.java
    │       │   │   ├── ProjectPermission.java
    │       │   │   ├── TaskStatus.java
    │       │   │   └── TaskPriority.java
    │       │   └── exception/                   # Domain exceptions
    │       │       └── InvalidTokenException.java
    │       │
    │       ├── application/                     # 🟠 APPLICATION LAYER
    │       │   ├── usecase/                     # Use case implementations
    │       │   │   ├── auth/
    │       │   │   │   ├── RegisterUseCaseImpl.java
    │       │   │   │   ├── LoginUseCaseImpl.java
    │       │   │   │   ├── LogoutUseCaseImpl.java
    │       │   │   │   ├── RefreshTokenUseCaseImpl.java
    │       │   │   │   ├── ForgotPasswordUseCaseImpl.java
    │       │   │   │   ├── ResetPasswordUseCaseImpl.java
    │       │   │   │   └── GetCurrentUserUseCaseImpl.java
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
    │       │   │   │   │   ├── RegisterUseCase.java
    │       │   │   │   │   ├── LoginUseCase.java
    │       │   │   │   │   ├── LogoutUseCase.java
    │       │   │   │   │   ├── RefreshTokenUseCase.java
    │       │   │   │   │   ├── ForgotPasswordUseCase.java
    │       │   │   │   │   ├── ResetPasswordUseCase.java
    │       │   │   │   │   └── GetCurrentUserUseCase.java
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
    │       │   │       │   ├── UserAuthPersistencePort.java
    │       │   │       │   └── EmailServicePort.java
    │       │   │       ├── user/
    │       │   │       │   └── UserPersistencePort.java
    │       │   │       └── workspace/
    │       │   │           └── WorkspacePersistencePort.java
    │       │   ├── dto/                         # Application-level DTOs
    │       │   │   ├── request/
    │       │   │   │   ├── RegisterRequest.java
    │       │   │   │   ├── LoginRequest.java
    │       │   │   │   ├── LogoutRequest.java
    │       │   │   │   ├── RefreshTokenRequest.java
    │       │   │   │   ├── ForgotPasswordRequest.java
    │       │   │   │   ├── ResetPasswordRequest.java
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
    │           ├── config/
    │           │   ├── SecurityConfig.java      # Spring Security + JWT filter
    │           │   └── OpenApiConfig.java       # Swagger/OpenAPI config
    │           ├── security/
    │           │   ├── JwtAuthenticationFilter.java
    │           │   ├── JwtService.java
    │           │   ├── JwtProperties.java
    │           │   ├── UserDetailsImpl.java
    │           │   └── UserDetailsServiceImpl.java
    │           └── service/
    │               └── EmailServiceStub.java    # Stub for email service
    │
    └── resources/
        ├── application.properties               # Main config
        └── db/
            └── migration/
                ├── V1__Initial_schema.sql       # Flyway migration
                ├── V2__Add_refresh_token_to_users.sql
                └── V3__add_password_reset_token_to_users.sql
```

---

## Layer Responsibilities

### 🟡 Domain Layer
The heart of the application. Contains business logic with **minimal dependencies**.

| Component | Purpose |
|---|---|
| `entity/` | Core business objects with JPA annotations (pragmatic approach) |
| `enums/` | Domain enumerations (statuses, roles, permissions) |
| `exception/` | Business rule violation exceptions |

> **Note:** Domain entities use JPA annotations directly (pragmatic approach for MVP). No separate JPA entities.

---

### 🟠 Application Layer
Orchestrates the flow of data and coordinates domain objects to fulfil use cases.

| Component | Purpose |
|---|---|
| `usecase/` | One class per business use case |
| `port/in/` | Input port interfaces (what the use case exposes) |
| `port/out/` | Output port interfaces (what the use case needs from outside) |
| `dto/` | Data structures crossing the application boundary |
| `mapper/` | MapStruct converters between domain objects and DTOs |

> **Rule:** Depends only on the Domain layer. No Spring Web, no JPA in use case logic.

---

### 🔵 Interface Adapter Layer
Converts data between formats convenient for use cases and external agencies.

| Component | Purpose |
|---|---|
| `adapter/in/web/` | REST controllers — call input ports |
| `adapter/out/persistence/` | Implements output ports using JPA/DB |

> **Rule:** Depends on Application layer ports. Adapters implement or use ports.

---

### 🔴 Infrastructure Layer
Wires everything together. Contains all framework-specific configuration.

| Component | Purpose |
|---|---|
| `config/` | Spring beans, security, Swagger, DB config |
| `security/` | JWT filters, token service, user details |
| `service/` | Infrastructure service implementations (email stub) |

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
HTTP POST /api/users
    │
    ▼
UserController (adapter/in/web)
    │  calls CreateUserUseCase
    ▼
CreateUserUseCaseImpl (application/usecase)
    │  uses domain entity + calls UserPersistencePort
    ▼
UserPersistenceAdapter (adapter/out/persistence)
    │  implements UserPersistencePort
    ▼
UserRepository (Spring Data JPA)
    │
    ▼
Database
```

---

## Key Principles

1. **Dependency Inversion** — Use cases define interfaces (ports); infrastructure implements them.
2. **Single Responsibility** — One use case per class.
3. **JPA on Domain Entities** — Pragmatic approach for MVP (no separate JPA entities).
4. **DTOs at boundaries** — Don't leak domain objects into controllers or persistence.
5. **Testability** — Domain and application layers should be testable without Spring context.

---

## Actual Dependencies (build.gradle)

```gradle
// Core Spring Boot (4.0.3)
spring-boot-starter
spring-boot-starter-web
spring-boot-starter-data-jpa
spring-boot-starter-validation
spring-boot-starter-security

// Database
flyway-core + flyway-database-postgresql
postgresql (runtime)

// JWT
jjwt-api + jjwt-impl + jjwt-jackson

// API Documentation
springdoc-openapi-starter-webmvc-ui (2.8.6)

// Mapping
mapstruct 1.6.3
mapstruct-processor 1.6.3 (annotation processor)

// Utilities
lombok (compileOnly + annotationProcessor)

// Testing
spring-boot-starter-test
```

---

## Current Implementation Status

### ✅ Implemented
- **Domain Layer**: All entities and enums defined with JPA annotations
- **Application Layer**: 
  - Auth use cases: Register, Login, Logout, Refresh Token, Forgot/Reset Password, Get Current User
  - User use cases: CRUD operations (extra, not in PRD)
  - Workspace use cases: CRUD operations
- **Adapter Layer**: Controllers and persistence adapters for Auth, User, Workspace
- **Infrastructure**: 
  - SecurityConfig with BCrypt password encoder
  - JWT authentication filter and service
  - OpenAPI/Swagger configuration
  - Method-level security enabled (`@EnableMethodSecurity`)
- **Database**: Flyway migrations (V1, V2, V3)

### 📋 Pending Implementations
- Global exception handler
- Remaining use cases (Projects, Tasks, Comments, Labels, Attachments, Notifications)
- Workspace member management
- Search functionality
- Audit logs

---

*Last updated: 2026-02-27 | Based on Robert C. Martin's Clean Architecture + Tom Hombergs' "Get Your Hands Dirty on Clean Architecture"*
