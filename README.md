# Task Management Backend API

A robust, RESTful backend service for task management — designed to be secure, performant, and extensible. Built with Java 17 and Spring Boot 4.0.3 following Clean Architecture principles.

---

## Features

- **Authentication & Authorization**: JWT-based auth with access/refresh tokens, role-based access control (RBAC)
- **Workspace Management**: Multi-tenant workspaces with member roles (owner, admin, member, guest)
- **Project Management**: Organize work into projects within workspaces
- **Task Management**: Full CRUD for tasks with priorities, statuses, and assignments
- **Collaboration**: Comments, labels, attachments (in progress)
- **API Documentation**: Auto-generated OpenAPI/Swagger UI

---

## Technology Stack

| Category | Technology |
|----------|------------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 4.0.3 |
| **Build Tool** | Gradle (Groovy DSL) |
| **Database** | PostgreSQL 15+ |
| **ORM** | Spring Data JPA + Hibernate |
| **Migrations** | Flyway |
| **Security** | Spring Security + JWT |
| **Documentation** | OpenAPI 3.0 (Springdoc) |
| **Mapping** | MapStruct |
| **Utilities** | Lombok |

---

## Architecture

This project follows **Clean Architecture** (Robert C. Martin) with 4 layers:

```
┌─────────────────────────────────────────────────────────┐
│  Infrastructure Layer (Config, Security, Filters)       │
├─────────────────────────────────────────────────────────┤
│  Adapter Layer                                          │
│  ├── In (Web): Controllers, DTOs                        │
│  └── Out (Persistence): Repositories, JPA Entities      │
├─────────────────────────────────────────────────────────┤
│  Application Layer                                      │
│  ├── Use Cases (Ports & Implementations)                │
│  ├── DTOs & Mappers                                     │
│  └── Port Interfaces (driving & driven)                 │
├─────────────────────────────────────────────────────────┤
│  Domain Layer                                           │
│  └── Entities, Enums, Business Rules                    │
└─────────────────────────────────────────────────────────┘
```

### Package Structure

```
com.afadhitya.taskmanagement
├── domain                    # Domain entities and enums
├── application
│   ├── dto
│   │   ├── request          # Request DTOs
│   │   └── response         # Response DTOs
│   ├── mapper               # MapStruct mappers
│   ├── port
│   │   ├── in               # Input ports (use case interfaces)
│   │   └── out              # Output ports (persistence interfaces)
│   └── usecase              # Use case implementations
├── adapter
│   ├── in
│   │   └── web              # REST controllers
│   └── out
│       └── persistence      # Repository adapters
└── infrastructure           # Config, security, etc.
```

---

## API Endpoints

### Implementation Status

| Category | Total | Done | Pending |
|----------|-------|------|---------|
| Authentication | 7 | 7 | 0 |
| Workspaces | 9 | 9 | 0 |
| Projects | 7 | 0 | 7 |
| Tasks | 8 | 0 | 8 |
| Comments | 4 | 0 | 4 |
| Labels | 6 | 0 | 6 |
| Attachments | 3 | 0 | 3 |
| Notifications | 4 | 0 | 4 |
| Search | 1 | 0 | 1 |
| Audit Logs | 1 | 0 | 1 |
| **Total** | **50** | **16** | **34** |

### Available Endpoints

#### Authentication (`/api/auth`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | User login |
| POST | `/api/auth/logout` | User logout |
| POST | `/api/auth/refresh-token` | Refresh access token |
| POST | `/api/auth/forgot-password` | Request password reset |
| POST | `/api/auth/reset-password` | Reset password with token |
| GET | `/api/auth/me` | Get current user info |

#### Workspaces (`/workspaces`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/workspaces` | Create workspace |
| GET | `/workspaces/{id}` | Get workspace by ID |
| PATCH | `/workspaces/{id}` | Update workspace |
| DELETE | `/workspaces/{id}` | Delete workspace |
| GET | `/workspaces/{id}/members` | List workspace members |
| POST | `/workspaces/{id}/members/invite` | Invite member to workspace |
| PATCH | `/workspaces/{id}/members/{userId}` | Update member role |
| DELETE | `/workspaces/{id}/members/{userId}` | Remove member from workspace |
| POST | `/workspaces/{id}/transfer-ownership` | Transfer workspace ownership |
| POST | `/workspaces/{id}/leave` | Leave workspace (non-owner) |

#### Health
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/health` | Health check (public) |

---

## Getting Started

### Prerequisites

- Java 17 or higher
- Docker & Docker Compose (for PostgreSQL)
- Gradle (or use wrapper `./gradlew`)

### Quick Start

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd taskmanagement
   ```

2. **Start PostgreSQL database**
   ```bash
   docker-compose up -d
   ```

3. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

4. **Access the API**
   - API Base URL: `http://localhost:8080`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`
   - OpenAPI Spec: `http://localhost:8080/api-docs`

### Build & Test

```bash
# Build the project
./gradlew build

# Run tests
./gradlew test
```

---

## Configuration

Application configuration is located in `config/application.yml`:

| Property | Description | Default |
|----------|-------------|---------|
| `spring.datasource.url` | PostgreSQL JDBC URL | `jdbc:postgresql://localhost:5432/taskmanagement` |
| `spring.datasource.username` | Database username | `taskuser` |
| `spring.datasource.password` | Database password | `taskpass` |
| `app.jwt.secret` | JWT signing key | (configurable) |
| `app.jwt.expiration-ms` | Access token expiration | 900000 (15 min) |
| `app.jwt.refresh-expiration-ms` | Refresh token expiration | 2592000000 (30 days) |

---

## Development Phases

| Phase | Scope | Status |
|-------|-------|--------|
| **Phase 1 — Core** | Auth, Users, Workspaces | ✅ In Progress |
| **Phase 2 — Collaboration** | Projects, Tasks, Comments | ⏳ Pending |
| **Phase 3 — Organization** | Labels, Filters, Search | ⏳ Pending |
| **Phase 4 — Enterprise** | RBAC, Audit logs, SSO | ⏳ Pending |
| **Phase 5 — Async & Scale** | Queues, WebSockets, Observability | ⏳ Pending |

---

## Security

- JWT-based authentication with short-lived access tokens (15 min) and long-lived refresh tokens (30 days)
- Password hashing with BCrypt (cost factor ≥ 12)
- Role-based access control at workspace and project levels
- Rate limiting and CORS protection

---

## License

This project is proprietary and confidential.

---

*Last updated: February 27, 2026*
