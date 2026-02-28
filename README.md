# Task Management Backend API

A robust, RESTful backend service for task management — designed to be secure, performant, and extensible. Built with Java 17 and Spring Boot 4.0.3 following Clean Architecture principles.

---

## Features

- **Authentication & Authorization**: JWT-based auth with access/refresh tokens, role-based access control (RBAC)
- **Workspace Management**: Multi-tenant workspaces with member roles (owner, admin, member, guest)
- **Project Management**: Organize work into projects with member management
- **Task Management**: Full CRUD for tasks with priorities, statuses, assignments, subtasks, and bulk operations
- **Collaboration**: Comments on tasks with update/delete capabilities
- **Organization**: Labels for categorizing tasks across workspaces
- **Search**: Global search across tasks, projects, and users within a workspace
- **Audit Logging**: Track all changes for enterprise compliance
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
| Projects | 7 | 7 | 0 |
| Tasks | 8 | 8 | 0 |
| Comments | 4 | 4 | 0 |
| Labels | 6 | 6 | 0 |
| Attachments | 3 | 0 | 3 |
| Notifications | 4 | 0 | 4 |
| Search | 1 | 1 | 0 |
| Audit Logs | 1 | 1 | 0 |
| **Total** | **50** | **43** | **7** |

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

#### Projects (`/workspaces/{workspaceId}/projects`, `/projects`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/workspaces/{workspaceId}/projects` | Create project |
| GET | `/workspaces/{workspaceId}/projects` | List projects in workspace |
| GET | `/projects/{id}` | Get project by ID |
| GET | `/projects/{id}/members` | List project members |
| PATCH | `/projects/{id}` | Update project |
| PATCH | `/projects/{id}/members/{userId}` | Update member role |
| DELETE | `/projects/{id}` | Delete project |
| POST | `/projects/{id}/members` | Add member to project |
| DELETE | `/projects/{id}/members/{userId}` | Remove member from project |

#### Tasks (`/projects/{projectId}/tasks`, `/tasks`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/projects/{projectId}/tasks` | Create task |
| GET | `/projects/{projectId}/tasks` | List tasks with filters, sort, pagination |
| GET | `/tasks/{id}` | Get task by ID |
| PATCH | `/tasks/{id}` | Update task |
| DELETE | `/tasks/{id}` | Delete task |
| POST | `/tasks/{id}/subtasks` | Create subtask |
| PATCH | `/projects/{projectId}/tasks/bulk` | Bulk update tasks |
| GET | `/users/me/tasks` | Get tasks assigned to current user |

#### Comments (`/tasks/{taskId}/comments`, `/comments`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/tasks/{taskId}/comments` | Add comment to task |
| GET | `/tasks/{taskId}/comments` | List comments on task |
| PATCH | `/comments/{id}` | Update comment |
| DELETE | `/comments/{id}` | Delete comment |

#### Labels (`/workspaces/{workspaceId}/labels`, `/projects/{projectId}/labels`, `/tasks/{taskId}/labels`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/workspaces/{workspaceId}/labels` | Create label |
| GET | `/projects/{projectId}/labels` | List labels in project |
| PATCH | `/labels/{id}` | Update label |
| DELETE | `/labels/{id}` | Delete label |
| POST | `/tasks/{taskId}/labels/{labelId}` | Assign label to task |
| DELETE | `/tasks/{taskId}/labels/{labelId}` | Remove label from task |

#### Search (`/search`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/search?q=&workspace_id=&type=` | Search tasks, projects, users |

#### Audit Logs (`/workspaces/{workspaceId}/audit-logs`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/workspaces/{workspaceId}/audit-logs` | List audit logs (paginated, filterable) |

#### Job Status (`/jobs`)
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/jobs/{jobId}` | Get bulk job status |

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
| **Phase 1 — Core** | Auth, Users, Workspaces | ✅ Complete |
| **Phase 2 — Collaboration** | Projects, Tasks, Comments, Labels | ✅ Complete |
| **Phase 3 — Organization** | Search, Bulk actions | ✅ Complete |
| **Phase 4 — Enterprise** | Audit Logs, RBAC hardening | ✅ Complete |
| **Phase 5 — Async & Scale** | Attachments, Notifications, Job Queues | 🔄 In Progress |

---

## Security

- JWT-based authentication with short-lived access tokens (15 min) and long-lived refresh tokens (30 days)
- Password hashing with BCrypt (cost factor ≥ 12)
- Role-based access control at workspace and project levels
- Method-level security with `@PreAuthorize` annotations
- Custom security expressions for fine-grained permissions

---

## License

This project is proprietary and confidential.

---

*Last updated: February 28, 2026*
