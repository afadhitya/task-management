# API Implementation Checkpoint

This file tracks the implementation status of all APIs defined in the PRD.
Each API should be marked as `done` once implemented and verified.

---

## Legend

- `[ ]` - Not implemented / Pending
- `[x]` - Implemented and verified

---

## 1. Authentication

| Status | Method | Endpoint | Description |
|--------|--------|----------|-------------|
| [x] | POST | `/api/auth/register` | Register new user |
| [x] | POST | `/api/auth/login` | User login |
| [x] | POST | `/api/auth/logout` | User logout |
| [x] | POST | `/api/auth/refresh-token` | Refresh access token |
| [x] | POST | `/api/auth/forgot-password` | Request password reset |
| [x] | POST | `/api/auth/reset-password` | Reset password with token |
| [x] | GET | `/api/auth/me` | Get current user info |
| [ ] | POST | `/api/auth/oauth/:provider` | OAuth login (Google, GitHub) - Post MVP |

---

## 2. Workspaces

| Status | Method | Endpoint | Description |
|--------|--------|----------|-------------|
| [x] | POST | `/workspaces` | Create workspace |
| [x] | GET | `/workspaces/:id` | Get workspace by ID |
| [x] | PATCH | `/workspaces/:id` | Update workspace |
| [x] | DELETE | `/workspaces/:id` | Delete workspace |
| [x] | GET | `/workspaces/:id/members` | List workspace members |
| [x] | POST | `/workspaces/:id/members/invite` | Invite member to workspace |
| [x] | PATCH | `/workspaces/:id/members/:userId` | Update member role |
| [x] | POST | `/workspaces/:id/transfer-ownership` | Transfer workspace ownership |
| [x] | DELETE | `/workspaces/:id/members/:userId` | Remove member from workspace |
| [x] | POST | `/workspaces/:id/leave` | Leave workspace (non-owner only) |

---

## 3. Projects

| Status | Method | Endpoint | Description |
|--------|--------|----------|-------------|
| [x] | POST | `/workspaces/:workspaceId/projects` | Create project |
| [x] | GET | `/workspaces/:workspaceId/projects` | List projects in workspace |
| [x] | GET | `/projects/:id` | Get project by ID |
| [x] | PATCH | `/projects/:id` | Update project |
| [x] | DELETE | `/projects/:id` | Delete project |
| [x] | GET | `/projects/:id/members` | List project members |
| [x] | POST | `/projects/:id/members` | Add member to project |
| [x] | PATCH | `/projects/:id/members/:userId` | Update member role |
| [x] | DELETE | `/projects/:id/members/:userId` | Remove member from project |

---

## 4. Tasks

| Status | Method | Endpoint | Description |
|--------|--------|----------|-------------|
| [x] | POST | `/projects/:projectId/tasks` | Create task |
| [x] | GET | `/projects/:projectId/tasks` | List tasks (with filters, sort, pagination) |
| [x] | GET | `/tasks/:id` | Get task by ID |
| [x] | PATCH | `/tasks/:id` | Update task |
| [x] | DELETE | `/tasks/:id` | Delete task |
| [x] | POST | `/tasks/:id/subtasks` | Create subtask |
| [x] | PATCH | `/tasks/bulk` | Bulk update tasks |
| [ ] | GET | `/users/me/tasks` | Get tasks assigned to current user |

---

## 5. Comments

| Status | Method | Endpoint | Description |
|--------|--------|----------|-------------|
| [ ] | POST | `/tasks/:taskId/comments` | Add comment to task |
| [ ] | GET | `/tasks/:taskId/comments` | List comments on task |
| [ ] | PATCH | `/comments/:id` | Update comment |
| [ ] | DELETE | `/comments/:id` | Delete comment |

---

## 6. Labels

| Status | Method | Endpoint | Description |
|--------|--------|----------|-------------|
| [ ] | POST | `/workspaces/:workspaceId/labels` | Create label |
| [ ] | GET | `/workspaces/:workspaceId/labels` | List labels in workspace |
| [ ] | PATCH | `/labels/:id` | Update label |
| [ ] | DELETE | `/labels/:id` | Delete label |
| [ ] | POST | `/tasks/:taskId/labels/:labelId` | Assign label to task |
| [ ] | DELETE | `/tasks/:taskId/labels/:labelId` | Remove label from task |

---

## 7. Attachments

| Status | Method | Endpoint | Description |
|--------|--------|----------|-------------|
| [ ] | POST | `/tasks/:taskId/attachments` | Upload attachment (multipart) |
| [ ] | GET | `/tasks/:taskId/attachments` | List task attachments |
| [ ] | DELETE | `/attachments/:id` | Delete attachment |

---

## 8. Notifications

| Status | Method | Endpoint | Description |
|--------|--------|----------|-------------|
| [ ] | GET | `/notifications` | List notifications (paginated) |
| [ ] | PATCH | `/notifications/:id/read` | Mark notification as read |
| [ ] | PATCH | `/notifications/read-all` | Mark all notifications as read |
| [ ] | DELETE | `/notifications/:id` | Delete notification |

---

## 9. Search

| Status | Method | Endpoint | Description |
|--------|--------|----------|-------------|
| [ ] | GET | `/search` | Search tasks, projects, users |

---

## 10. Audit Logs (Enterprise)

| Status | Method | Endpoint | Description |
|--------|--------|----------|-------------|
| [ ] | GET | `/workspaces/:id/audit-logs` | List audit logs (paginated, filterable) |

---

## 11. Health Check

| Status | Method | Endpoint | Description |
|--------|--------|----------|-------------|
| [x] | GET | `/health` | Health check endpoint (public) |

---

## Implementation Notes

### Endpoint Paths
Current implementation uses inconsistent path prefixes:
- `/api/auth/*` - Auth endpoints (with /api prefix)
- `/api/users/*` - User endpoints (with /api prefix) - **NOT part of PRD**
- `/workspaces/*` - Workspace endpoints (WITHOUT /api prefix)

**Note:** When implementing new endpoints, follow the PRD specification. Consider standardizing all endpoints to use `/api` prefix in a future refactoring task.

### User Controller (Non-PRD)
There's a `/api/users` CRUD controller that provides basic user management but is NOT part of the PRD specification. The PRD only specifies:
- `POST /auth/register` - Register new user
- `GET /auth/me` - Get current user info

The UserController may be refactored or removed in favor of auth-based user management.

### Authentication Flow
- JWT-based authentication with access tokens (15 min) and refresh tokens (30 days)
- Refresh tokens stored in database with rotation on use
- Password reset tokens with expiration (24 hours)
- Spring Security with method-level security enabled (`@EnableMethodSecurity`)

---

## Summary

| Category | Total | Done | Pending |
|----------|-------|------|---------|
| Authentication | 7 | 7 | 0 |
| Workspaces | 8 | 4 | 4 |
| Projects | 9 | 9 | 0 |
| Tasks | 8 | 7 | 1 |
| Comments | 4 | 0 | 4 |
| Labels | 6 | 0 | 6 |
| Attachments | 3 | 0 | 3 |
| Notifications | 4 | 0 | 4 |
| Search | 1 | 0 | 1 |
| Audit Logs | 1 | 0 | 1 |
| Health Check | 1 | 1 | 0 |
| **Total** | **52** | **28** | **24** |

---

*Last updated: 2026-02-27*
