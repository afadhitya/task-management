# Product Requirements Document
## Task Management App — Backend (API) Platform

**Version:** 1.2  
**Date:** February 27, 2026  
**Status:** Draft

---

## 1. Overview

### 1.1 Problem Statement
Teams and individuals need a reliable, scalable backend service to power task management workflows. The system must expose a well-structured API that any frontend client (web app, mobile app, third-party integration) can consume to manage tasks, users, projects, and collaboration.

### 1.2 Product Vision
A robust, RESTful backend service for task management — designed to be secure, performant, and extensible — serving individual users through enterprise-scale teams from a single platform.

### 1.3 Goals
- Provide a complete API surface for all task management operations
- Support multi-tenancy for individual, team, and enterprise workspaces
- Enforce role-based access control at every layer
- Be frontend-agnostic — any client can integrate against the API

---

## 2. Target Consumers of the API

| Consumer Type | Description |
|---|---|
| Web frontend clients | Browser-based SPAs or SSR apps |
| Mobile clients | iOS / Android apps (future) |
| Third-party integrations | Slack, GitHub, Zapier, etc. |
| Internal services | Notification workers, reporting jobs |
| API users / developers | Direct API access via tokens |

---

## 3. System Architecture

### 3.1 Architecture Style
- **Primary:** RESTful API (JSON over HTTPS)
- **Real-time:** WebSocket or Server-Sent Events (SSE) for live updates (notifications, task changes) — post-MVP

### 3.2 Core Components

```
┌─────────────────────────────────────────┐
│              API Gateway / Load Balancer │
└────────────────────┬────────────────────┘
                     │
        ┌────────────▼────────────┐
        │     Application Server  │
        │  (Auth / Business Logic)│
        └──┬──────────┬───────────┘
           │          │
   ┌───────▼──┐  ┌────▼──────────┐
   │ Primary  │  │  Cache Layer  │
   │    DB    │  │   (Redis)     │
   └───────┬──┘  └───────────────┘
           │
   ┌───────▼──────────┐
   │  Message Queue   │
   │ (Jobs/Events)    │
   └──────────────────┘
```

### 3.3 Technology Stack
- **Language & Framework:** Java 17 Spring Boot 4.0.3
- **API Layer:** Spring Web MVC (REST controllers)
- **Database:** PostgreSQL 15+ (primary relational store)
- **ORM:** Spring Data JPA + Hibernate
- **Database Migrations:** Flyway
- **Cache:** Redis (session store, rate limiting, real-time pub/sub via Spring Cache + Spring Data Redis)
- **Queue:** Spring Batch / Spring Scheduler for cron jobs; RabbitMQ or AWS SQS for async event-driven jobs
- **Auth:** Spring Security + JWT (access + refresh tokens); OAuth 2.0 (Spring Security OAuth2 Client)
- **Storage:** S3-compatible object storage for file attachments (AWS S3 / MinIO via Spring Cloud AWS)
- **Docs:** OpenAPI 3.0 via Springdoc (auto-generated Swagger UI at `/swagger-ui.html`)
- **Testing:** JUnit 5, Mockito, Spring Boot Test, Testcontainers (PostgreSQL for integration tests)
- **Build Tool:** Gradle (with Groovy DSL)

---

## 4. Data Models

### 4.1 Core Entities

**User**
```
id, email, password_hash, full_name, avatar_url,
created_at, updated_at, last_login_at, is_active,
refresh_token, password_reset_token, password_reset_token_expires_at
```

**Workspace**
```
id, name, slug, logo_url, owner_id,
plan_tier (free | team | enterprise),
created_at, updated_at
```

**WorkspaceMember**
```
id, workspace_id, user_id,
role (owner | admin | member | guest),
invited_by, joined_at
```

**Project**
```
id, workspace_id, name, description, color,
status (active | archived), created_by,
created_at, updated_at
```

**ProjectMember**
```
id, project_id, user_id,
permission (view | contributor | manager)
```

**Task**
```
id, project_id, parent_task_id (nullable — for subtasks),
title, description, status (todo | in_progress | done | blocked),
priority (low | medium | high | urgent),
assignee_ids[], due_date, created_by,
position (for ordering), created_at, updated_at
```

**Label**
```
id, workspace_id, name, color
```

**TaskLabel** *(join table)*
```
id, task_id, label_id
```

**Comment**
```
id, task_id, author_id, body,
parent_comment_id (nullable — for threads),
created_at, updated_at, is_deleted
```

**Attachment**
```
id, task_id, uploaded_by, file_name,
file_size, mime_type, storage_url, created_at
```

**Notification**
```
id, user_id, type, payload (JSON),
is_read, created_at
```

**AuditLog** *(enterprise)*
```
id, workspace_id, actor_id, action,
entity_type, entity_id, diff (JSON), created_at
```

---

## 5. API Endpoints

### 5.1 Authentication
```
POST   /api/auth/register
POST   /api/auth/login
POST   /api/auth/logout
POST   /api/auth/refresh-token
POST   /api/auth/forgot-password
POST   /api/auth/reset-password
GET    /api/auth/me
POST   /auth/oauth/:provider          (Google, GitHub) - Post MVP
```

### 5.2 Workspaces
```
POST   /workspaces
GET    /workspaces/:id
PATCH  /workspaces/:id
DELETE /workspaces/:id
GET    /workspaces/:id/members
POST   /workspaces/:id/members/invite
PATCH  /workspaces/:id/members/:userId
DELETE /workspaces/:id/members/:userId
```

### 5.3 Projects
```
POST   /workspaces/:workspaceId/projects
GET    /workspaces/:workspaceId/projects
GET    /projects/:id
PATCH  /projects/:id
DELETE /projects/:id
POST   /projects/:id/members
DELETE /projects/:id/members/:userId
```

### 5.4 Tasks
```
POST   /projects/:projectId/tasks
GET    /projects/:projectId/tasks        (supports filters, sort, pagination)
GET    /tasks/:id
PATCH  /tasks/:id
DELETE /tasks/:id
POST   /tasks/:id/subtasks
PATCH  /tasks/bulk                       (bulk status/assignee update)
GET    /users/me/tasks                   (tasks assigned to current user)
```

### 5.5 Comments
```
POST   /tasks/:taskId/comments
GET    /tasks/:taskId/comments
PATCH  /comments/:id
DELETE /comments/:id
```

### 5.6 Labels
```
POST   /workspaces/:workspaceId/labels
GET    /workspaces/:workspaceId/labels
PATCH  /labels/:id
DELETE /labels/:id
POST   /tasks/:taskId/labels/:labelId
DELETE /tasks/:taskId/labels/:labelId
```

### 5.7 Attachments
```
POST   /tasks/:taskId/attachments        (multipart upload)
GET    /tasks/:taskId/attachments
DELETE /attachments/:id
```

### 5.8 Notifications
```
GET    /notifications                    (paginated)
PATCH  /notifications/:id/read
PATCH  /notifications/read-all
DELETE /notifications/:id
```

### 5.9 Search
```
GET    /search?q=&workspace_id=&type=    (tasks, projects, users)
```

### 5.10 Audit Logs *(Enterprise)*
```
GET    /workspaces/:id/audit-logs        (paginated, filterable)
```

---

## 6. Authentication & Authorization

### 6.1 Authentication Flow
- JWT-based with short-lived access tokens (15 min) and long-lived refresh tokens (30 days)
- Refresh tokens stored in database with rotation on use
- Password reset tokens with 24-hour expiration
- OAuth 2.0 support: Google (MVP), GitHub (post-MVP)
- Enterprise: SAML 2.0 / SSO (post-MVP)

### 6.2 Authorization Model (RBAC)
| Role | Scope | Capabilities |
|---|---|---|
| Owner | Workspace | Full access, billing, delete workspace |
| Admin | Workspace | Manage members, projects, settings |
| Member | Workspace | Create/edit projects and tasks they have access to |
| Guest | Project | View/comment only on specific projects (no workspace access) |

Project-level permissions override workspace defaults where stricter. See `/docs/project-permissions.md` for detailed resolution rules.

### 6.3 Security Filter Chain (Spring Security)
```
Request → CORS Filter → Rate Limit Filter → JWT Auth Filter → RBAC (Method Security) → Controller
```

---

## 7. Non-Functional Requirements

### 7.1 Performance
- API response time P95 < 300ms for standard reads
- Support 1,000 concurrent requests (MVP); horizontally scalable for enterprise
- Pagination required on all list endpoints (default limit: 50, max: 200)

### 7.2 Security
- All endpoints served over HTTPS (TLS 1.2+)
- Passwords hashed with bcrypt (cost factor ≥ 12)
- Input validation and sanitization on all endpoints (prevent SQL injection, XSS payloads)
- Rate limiting: 100 req/min per user; 1,000 req/min per API key
- CORS policy configurable per workspace (enterprise)
- GDPR: data export and account deletion endpoints required

### 7.3 Reliability
- 99.9% uptime target
- Database connection pooling
- Graceful shutdown handling
- Health check endpoint: `GET /health`

### 7.4 Observability
- Structured JSON logging (request ID, user ID, latency, status)
- Error tracking (Sentry or equivalent)
- Metrics: request rate, error rate, DB query latency (Prometheus/Grafana)
- Distributed tracing (OpenTelemetry — post-MVP)

---

## 8. Background Jobs & Workers

| Job | Trigger | Description |
|---|---|---|
| Email notifications | Task event (async via RabbitMQ) | Send assignment / mention / due date emails via Spring Mail |
| Due date reminders | `@Scheduled` cron (daily) | Notify users of tasks due in 24hrs |
| Recurring task generation | `@Scheduled` cron (configurable) | Auto-create next instance of recurring tasks |
| Attachment virus scan | Upload event (async) | Scan files before marking as available |
| Audit log archival | `@Scheduled` cron (monthly) | Archive old logs to cold storage (enterprise) |
| Search index sync | DB change event (Debezium CDC or app-level) | Keep search index up to date |

---

## 9. Out of Scope (v1.0)

- Any frontend UI or web client
- Native mobile API optimizations (GraphQL, offline sync) — v2
- Built-in video/voice/screen share
- Billing and subscription management (use Stripe directly)
- AI/ML features (smart prioritization, NLP task parsing)
- White-labeling infrastructure

---

## 10. Success Metrics

| Metric | Target |
|---|---|
| API uptime | ≥ 99.9% |
| P95 response time | < 300ms |
| Error rate (5xx) | < 0.1% |
| Test coverage | ≥ 80% unit + integration |
| Time to first API response (onboarding) | < 5 min with docs |

---

## 11. Proposed Development Phases

| Phase | Scope | Duration | Status |
|---|---|---|---|
| **Phase 1 — Core** | Auth, Users, Workspaces, Projects, Tasks (CRUD) | 4 weeks | In Progress |
| **Phase 2 — Collaboration** | Comments, Assignees, Notifications, Attachments | 3 weeks | Pending |
| **Phase 3 — Organization** | Labels, Filters, Search, Bulk actions | 2 weeks | Pending |
| **Phase 4 — Enterprise** | RBAC hardening, Audit logs, SSO, Rate limiting | 3 weeks | Pending |
| **Phase 5 — Async & Scale** | Job queues, Recurring tasks, WebSockets, Observability | Ongoing | Pending |

---

## 12. Current Implementation Notes

### ✅ Completed (Phase 1 - Partial)
- **Authentication**: All 7 core endpoints implemented (register, login, logout, refresh-token, forgot-password, reset-password, me)
- **Workspaces**: Basic CRUD operations (create, get, update, delete)
- **Infrastructure**: JWT authentication, Spring Security, OpenAPI/Swagger, Flyway migrations

### 🔧 Known Issues
- **Inconsistent API paths**: Auth uses `/api/auth/*`, Workspaces use `/workspaces/*` (without /api prefix)
- **Missing UserController in PRD**: The `/api/users` CRUD endpoints exist but are not part of PRD specification

### 📋 Next Priority
1. Workspace member management endpoints
2. Project CRUD operations
3. Task CRUD operations
4. Global exception handler

---

## 13. Open Questions

1. ~~Will the project use **Maven or Gradle** as the build tool?~~ **Resolved:** Using Gradle with Groovy DSL
2. ~~Should the API be versioned from day one via URL path (e.g. `/api/v1/`)?~~ **Resolved:** Partially implemented - Auth uses `/api/auth`, Workspaces use `/workspaces` (inconsistent, needs standardization)
3. What is the expected scale at launch — how many workspaces / concurrent users?
4. ~~Is there a preference for **self-hosted PostgreSQL** vs. a managed service (AWS RDS, Supabase)?~~ **Resolved:** Using Docker Compose for local development with PostgreSQL 15
5. Should Redis be used for both caching and async pub/sub, or will a dedicated message broker (RabbitMQ) be introduced from the start?
6. Are there existing internal services this API needs to integrate with?
7. What is the deployment target — Docker + Kubernetes, AWS ECS, bare metal, or other?

---

*Document Owner: Product / Engineering Team*  
*Last Updated: February 27, 2026*
