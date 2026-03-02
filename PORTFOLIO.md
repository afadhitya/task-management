# Task Management System - Technical Portfolio

## Overview

A production-ready Task Management REST API built with modern Java technologies, implementing enterprise-grade patterns for scalability, security, and maintainability.

---

## Technology Stack

### Core Technologies
| Technology | Version | Purpose |
|------------|---------|---------|
| Java | 17 | Programming language |
| Spring Boot | 4.0.3 | Application framework |
| Spring Security | 6.x | Authentication & Authorization |
| Spring Data JPA | - | Data persistence |
| PostgreSQL | - | Relational database |

### Supporting Technologies
| Technology | Purpose |
|------------|---------|
| Flyway | Database migrations |
| MapStruct | DTO/Entity mapping |
| JWT (jjwt) | Token-based authentication |
| Caffeine | High-performance caching |
| SpringDoc OpenAPI | API documentation (Swagger) |
| Lombok | Boilerplate reduction |

---

## Key Implementations

### 1. Clean Architecture (4-Layer)

**Structure:**
```
com.afadhitya.taskmanagement/
├── domain/          # Entities, Enums (business logic)
├── application/     # Use Cases, Ports, DTOs
├── adapter/         # Controllers (in), Persistence (out)
└── infrastructure/  # Security, Configuration
```

**Benefits:**
- **Separation of Concerns** - Each layer has clear responsibilities
- **Testability** - Business logic isolated from infrastructure
- **Maintainability** - Easy to modify or replace layers
- **Scalability** - Can evolve independently

---

### 2. Audit Logging System

**Implementation:** Decorator Pattern with Transaction Isolation

**Key Files:**
- `application/usecase/audit/AuditedTaskUseCases.java`
- `application/usecase/audit/AuditedWorkspaceUseCases.java`

**Features:**
- Tracks CREATE, UPDATE, DELETE operations
- Calculates **diff** for UPDATE operations (old → new values)
- Separate transaction (`REQUIRES_NEW`) for audit logs
- Feature-based audit toggle per workspace

**Example Diff Tracking:**
```java
if (request.title() != null && !request.title().equals(task.getTitle())) {
    diff.put("title", Map.of("old", task.getTitle(), "new", request.title()));
}
```

**Benefits:**
- **Compliance** - Full audit trail for enterprise requirements
- **Troubleshooting** - Easy to trace changes and identify issues
- **Non-blocking** - Audit failures don't affect main operations
- **Selective** - Can be enabled/disabled per workspace

---

### 3. Role-Based Access Control (RBAC)

**Implementation:** Method-Level Security with Custom Expressions

**Security Expressions:**
- `@projectSecurity.canViewProject(#id)` - View access
- `@projectSecurity.canContributeToProject(#id)` - Create/Edit
- `@projectSecurity.canManageProject(#id)` - Delete/Admin
- `@taskSecurity.canContributeToTask(#id)` - Task-level permissions
- `@workspaceSecurity.canManageWorkspace(#id)` - Workspace admin

**Example Usage:**
```java
@PreAuthorize("@projectSecurity.canContributeToProject(#projectId)")
@PostMapping("/projects/{projectId}/tasks")
public ResponseEntity<TaskResponse> createTask(...) { }
```

**Benefits:**
- **Fine-grained** - Permission at resource level, not just endpoint
- **Declarative** - Security visible in controller
- **Reusable** - Security expressions can be used across controllers
- **Flexible** - Easy to add new permission checks

---

### 4. Feature Flag & Entitlement System

**Implementation:** Dispatcher Pattern with Strategy Handlers

**Key Components:**
- `FeatureDispatcher` - Routes to appropriate handlers
- `FeatureHandler` - Interface for feature logic
- `FeatureTogglePort` - Check if feature is enabled
- Plan-based limits (FREE, PRO, ENTERPRISE)

**Key Files:**
- `application/usecase/feature/InviteMemberFeatureDispatcher.java`
- `application/usecase/feature/handler/MemberLimitFeatureHandler.java`

**Example Limit Enforcement:**
```java
int currentMemberCount = workspacePersistencePort.countMembers(workspaceId);
int maxAllowed = featureTogglePort.getLimit(workspaceId, LimitType.MAX_MEMBERS);

if (maxAllowed >= 0 && currentMemberCount >= maxAllowed) {
    throw new PlanLimitExceededException(...);
}
```

**Features Implemented:**
- Member limits per plan tier
- Project limits per plan tier
- Audit log feature toggle

**Benefits:**
- **Monetization-ready** - Easy to add paid features
- **A/B Testing** - Enable features per workspace
- **Gradual Rollout** - Feature flags for gradual deployment
- **Plan Enforcement** - Hard limits based on subscription

---

### 5. Bulk Job Processing (Event-Driven)

**Implementation:** Spring Events + Async Processing

**Key Components:**
- `BulkJobSubmittedEvent` - Event published on submission
- `BulkJobEventListener` - Async listener with `@TransactionalEventListener`
- `BulkJobProcessor` - Processes job with progress tracking

**Key Files:**
- `application/usecase/bulkjob/BulkJobProcessor.java`
- `application/event/BulkJobEventListener.java`

**Features:**
- Async processing with `@Async`
- Transaction-aware (processes after commit)
- Progress tracking (processed/failed counts)
- Error handling with failure count

```java
@Async
@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
public void handleBulkJobSubmitted(BulkJobSubmittedEvent event) {
    bulkJobProcessor.processBulkUpdateTasks(event.getJobId(), event.getRequest());
}
```

**Benefits:**
- **Non-blocking** - Users get immediate response
- **Reliable** - Only processes after successful commit
- **Scalable** - Can run on separate thread/instance
- **Trackable** - Job status available to users

---

### 6. JWT Authentication

**Implementation:** Filter-based Token Processing

**Key Files:**
- `infrastructure/security/JwtAuthenticationFilter.java`
- `infrastructure/security/JwtService.java`
- `infrastructure/security/JwtProperties.java`

**Features:**
- Access token + Refresh token support
- Token validation on every request
- User details loaded from database
- Security context populated

**Benefits:**
- **Stateless** - No server-side session storage
- **Scalable** - Works across multiple instances
- **Secure** - Cryptographically signed tokens
- **Flexible** - Can implement token blacklisting

---

### 7. Multi-Entity Search

**Implementation:** Single Search Use Case with Type Filtering

**Key Files:**
- `application/usecase/search/SearchUseCaseImpl.java`
- `adapter/in/web/SearchController.java`

**Supported Entities:**
- Tasks (by title, description)
- Projects (by name)
- Users (by name, email)

**API Usage:**
```
GET /api/search?workspaceId=1&query=bug&type=TASKS
GET /api/search?workspaceId=1&query=project
GET /api/search?workspaceId=1&query=john&type=USERS
```

**Benefits:**
- **Unified** - Single endpoint for all searches
- **Flexible** - Filter by type or search all
- **Scoped** - Only returns data from user's workspace

---

### 8. High-Performance Caching

**Implementation:** Caffeine Cache with TTL

**Key Files:**
- `infrastructure/config/CacheConfig.java`

**Configuration:**
```java
Caffeine.newBuilder()
    .expireAfterWrite(5, TimeUnit.MINUTES)
    .maximumSize(10000)
    .recordStats()
```

**Cached Data:**
- Feature flags
- Plan limits
- Permission checks

**Benefits:**
- **Fast** - In-memory caching (Guava/Caffeine)
- **TTL-based** - Automatic expiration
- **Observable** - Stats recording for monitoring
- **Scalable** - 10,000 entries capacity

---

### 9. Database Migrations

**Implementation:** Flyway

**Migration Files:**
- V1: Initial schema
- V2: Refresh token support
- V3: Password reset tokens
- V4: Bulk jobs table
- V5: Labels table updates
- V6: Feature flag system
- V7: User role

**Benefits:**
- **Versioned** - Full history of schema changes
- **Reproducible** - Same schema on all environments
- **Atomic** - Migrate as a team
- **Safe** - Idempotent migrations

---

### 10. API Documentation

**Implementation:** SpringDoc OpenAPI (Swagger)

**Features:**
- Auto-generated from code
- JWT authentication support
- Request/Response schemas
- Example values

**Access:** `/swagger-ui.html`

**Benefits:**
- **Self-documenting** - Always up-to-date
- **Interactive** - Test APIs directly
- **Developer-friendly** - Easy onboarding

---

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                        ADAPTER LAYER                           │
│  ┌─────────────────────┐  ┌─────────────────────────────────┐  │
│  │   REST Controllers  │  │  Persistence Adapters (JPA)     │  │
│  │  - TaskController   │  │  - TaskPersistenceAdapter        │  │
│  │  - ProjectController│  │  - ProjectPersistenceAdapter     │  │
│  │  - WorkspaceController│ │  - WorkspacePersistenceAdapter  │  │
│  └──────────┬──────────┘  └───────────────┬─────────────────┘  │
└─────────────┼──────────────────────────────┼────────────────────┘
              │                              │
              ▼                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                      APPLICATION LAYER                         │
│  ┌─────────────────────┐  ┌─────────────────────────────────┐  │
│  │     Use Cases       │  │         Ports (Interfaces)      │  │
│  │  - CreateTask       │  │  - TaskPersistencePort          │  │
│  │  - UpdateTask       │  │  - ProjectPersistencePort       │  │
│  │  - SearchUseCase    │  │  - FeatureTogglePort             │  │
│  └──────────┬──────────┘  └─────────────────────────────────┘  │
│             │                                                   │
│  ┌──────────┴──────────┐  ┌─────────────────────────────────┐  │
│  │  Decorators        │  │       Event Listeners           │  │
│  │  - AuditedTask     │  │  - BulkJobEventListener        │  │
│  │  - FeatureDispatcher│ │                                 │  │
│  └───────────────────┘  └─────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────────────────────────────┐
│                       DOMAIN LAYER                             │
│  ┌────────────────┐  ┌────────────────┐  ┌─────────────────┐   │
│  │    Entities    │  │     Enums      │  │   Exceptions    │   │
│  │  - Task        │  │  - TaskStatus  │  │  - Unauthorized │   │
│  │  - Project     │  │  - UserRole    │  │  - PlanLimit    │   │
│  │  - Workspace   │  │  - PlanTier    │  │                 │   │
│  └────────────────┘  └────────────────┘  └─────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
              │
              ▼
┌─────────────────────────────────────────────────────────────────┐
│                   INFRASTRUCTURE LAYER                         │
│  ┌─────────────────────┐  ┌─────────────────────────────────┐   │
│  │    Security         │  │       Configuration            │   │
│  │  - JWT Filter       │  │  - CacheConfig                 │   │
│  │  - SecurityExpr     │  │  - AsyncConfig                 │   │
│  │  - JwtService       │  │  - SecurityConfig              │   │
│  └─────────────────────┘  └─────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

---

## API Endpoints Overview

| Resource | Endpoints |
|----------|-----------|
| Auth | POST /register, POST /login, POST /refresh |
| Workspace | CRUD + invite member, remove member, transfer ownership |
| Project | CRUD + add/remove members, update roles |
| Task | CRUD + subtasks, bulk update, assign labels |
| Label | CRUD + assign to task |
| Search | GET /search |
| Audit | GET /audit-logs |
| Jobs | GET /jobs/{id}/status |

---

## What Makes This Portfolio Valuable

### 1. **Enterprise-Ready Patterns**
- Clean Architecture for maintainability
- Decorator pattern for cross-cutting concerns
- Event-driven for async processing

### 2. **Security-First Design**
- JWT authentication with refresh tokens
- RBAC at method level
- Resource-based permissions

### 3. **Scalability Considerations**
- Caching for frequently accessed data
- Async processing for long-running operations
- Event-driven architecture

### 4. **Production Features**
- Database migrations
- API documentation
- Audit logging
- Feature flags

### 5. **Modern Java Practices**
- Java 17 with records
- Builder pattern with Lombok
- Constructor injection
- MapStruct for mapping

---

## AI Collaboration: Human-in-the-Loop Development

> This project demonstrates not just code implementation, but the ability to **leverage AI as a powerful development tool** while maintaining architectural ownership.

### How This Project Was Built

This project was **NOT pure "vibe coding"** - it was built through a structured collaboration between human oversight and AI execution:

| Phase | Human Role | AI Role |
|-------|-----------|---------|
| **Planning** | Define requirements, discuss options with AI | Suggest alternatives, tradeoffs, recommendations |
| **Design** | Evaluate AI suggestions, make final decisions | Present architecture options, pattern comparisons |
| **Implementation** | Review & validate AI-generated code | Generate code based on specs |
| **Refinement** | Request changes, enforce code style | Iterate based on feedback |
| **Validation** | Manual code review, testing | Generate tests |

### AI as a Thinking Partner

During **Planning & Design**, I used AI to explore alternatives and make informed decisions:

**Example Discussions:**
- *"What are the tradeoffs between Clean Architecture vs layered architecture for a SaaS app?"*
- *"Should I use JPA entities separately or reuse domain entities? Pros/cons?"*
- *"What's better for audit logging: Event Sourcing vs Decorator Pattern vs AOP?"*
- *"Feature flags at database level vs in-memory: which scales better?"*

**Decisions Made After AI Discussions:**
- Clean Architecture chosen for separation of concerns and testability
- Domain entities with JPA annotations (pragmatic approach vs pure DDD)
- Decorator Pattern for audit (simpler than Event Sourcing, more explicit than AOP)
- Database-backed feature flags for multi-tenant SaaS requirements

### Human Decisions That Shaped This Project

1. **Architecture Choice** - Selected Clean Architecture (4-layer) after evaluating alternatives
2. **Technology Stack** - Picked Java 17 + Spring Boot 4.x + PostgreSQL for production-readiness
3. **Code Patterns** - Enforced:
   - Builder pattern with Lombok
   - Constructor injection
   - MapStruct for DTO mapping
   - Immutable objects where possible
4. **Security Design** - Chose RBAC with method-level `@PreAuthorize` for fine-grained control
5. **Feature Decisions** - Approved specific implementations (decorator for audit, event-driven for bulk jobs)

### What I Checked Manually

- **Code correctness** - Every implementation reviewed before acceptance
- **Security** - RBAC logic, JWT implementation, input validation
- **Database schema** - Flyway migrations reviewed for data integrity
- **Error handling** - Proper exceptions, global exception handler
- **Code consistency** - Naming conventions, package structure

### Value This Adds

This approach demonstrates:

- **AI Collaboration Skills** - Uses AI as a thinking partner, not just a code generator
- **AI Tool Mastery** - Knows how to prompt, review, and refine AI output
- **Architectural Vision** - Can design systems, not just implement - with AI's help weighing tradeoffs
- **Code Quality Standards** - Enforces consistent style and patterns
- **Critical Thinking** - Validates AI suggestions, catches potential issues
- **Iterative Development** - Breaks work into phases with validation checkpoints

### Why This Matters

In the age of AI-assisted development, the skill isn't just writing code - it's:
- Knowing **what** to build (requirements & architecture) - with AI helping explore options
- Knowing **how** to build it (patterns & best practices) - with AI discussing tradeoffs
- Knowing **what's wrong** when AI produces suboptimal code
- Knowing **when to override** AI suggestions
- Knowing **how to collaborate** with AI as a peer, not a subordinate

This project proves all five.

---

## Running the Project

```bash
# Build
./gradlew build

# Run
./gradlew bootRun

# Test
./gradlew test
```

**Environment Variables:**
- `DB_URL` - PostgreSQL connection
- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password
- `JWT_SECRET` - JWT signing key
