# Project Permissions Model

## Overview

This document describes how project-level permissions work alongside workspace roles to control access to projects.

## Concepts

### WorkspaceRole (Workspace-wide)
Defines a user's role within the entire workspace:
- **OWNER** - Full workspace control, billing, member management
- **ADMIN** - Can manage workspace settings, invite members, create projects
- **MEMBER** - Can participate in projects they have access to
- **GUEST** - Limited access, requires explicit project assignment

### ProjectPermission (Project-specific)
Defines what actions a user can perform on a specific project:
- **VIEW** - Read-only: view tasks, comments, attachments
- **CONTRIBUTOR** - Can create/edit tasks, add comments, manage task labels
- **MANAGER** - Can manage project settings, members, and delete the project

## Permission Resolution

The effective permission is determined by combining the user's workspace role with any project-specific permission:

### Resolution Rules

1. **Workspace Owner Override**
   - Always has MANAGER level on all projects
   - Project permission settings are ignored
   - Cannot be locked out of any project

2. **Guest Access**
   - Must have explicit `ProjectMember` entry to access a project
   - Workspace role is ignored for project access
   - Can be assigned VIEW, CONTRIBUTOR, or MANAGER

3. **Admin/Member with Project Permission**
   - Project permission acts as a **ceiling** (restrictive model)
   - If set to VIEW, Admin can only view (normally would have MANAGER)
   - Used to restrict access on sensitive projects

4. **Default Permissions (when no project entry exists)**

| Workspace Role | Default Project Permission |
|---------------|---------------------------|
| ADMIN | MANAGER |
| MEMBER | CONTRIBUTOR |
| GUEST | No access (requires explicit entry) |

### Resolution Matrix

| Workspace Role | No Project Entry | VIEW | CONTRIBUTOR | MANAGER |
|----------------|------------------|------|-------------|---------|
| **OWNER** | MANAGER | MANAGER | MANAGER | MANAGER |
| **ADMIN** | MANAGER (default) | VIEW | CONTRIBUTOR | MANAGER |
| **MEMBER** | CONTRIBUTOR (default) | VIEW | CONTRIBUTOR | MANAGER |
| **GUEST** | ❌ No access | VIEW | CONTRIBUTOR | MANAGER |

## Use Cases

### 1. Restrict an Admin on a Sensitive Project
A workspace Admin is assigned Project VIEW permission → can only view, not modify.

### 2. Elevate a Member to Project Manager
A workspace Member is assigned Project MANAGER permission → can manage this specific project.

### 3. Guest Collaboration
External contractor added as Guest to workspace + Project CONTRIBUTOR → can work on specific project only.

### 4. Owner Always Has Access
Even if project permission is not set or restricted, Workspace Owner retains full control.

## Implementation

### Permission Checking

```java
@Component
public class PermissionService {
    
    public boolean canManageProject(User user, Project project) {
        ProjectPermission effective = resolveEffectivePermission(user, project);
        return effective == ProjectPermission.MANAGER;
    }
    
    public boolean canContributeToProject(User user, Project project) {
        ProjectPermission effective = resolveEffectivePermission(user, project);
        return effective == ProjectPermission.MANAGER 
            || effective == ProjectPermission.CONTRIBUTOR;
    }
    
    public boolean canViewProject(User user, Project project) {
        ProjectPermission effective = resolveEffectivePermission(user, project);
        return effective != null; // Any permission level allows viewing
    }
}
```

### Security Annotation Usage

```java
@RestController
@RequestMapping("/projects/{projectId}")
public class ProjectController {
    
    @GetMapping
    @PreAuthorize("@permissionService.canViewProject(principal, #projectId)")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable Long projectId) {
        // ...
    }
    
    @PostMapping("/tasks")
    @PreAuthorize("@permissionService.canContributeToProject(principal, #projectId)")
    public ResponseEntity<TaskResponse> createTask(@PathVariable Long projectId, @RequestBody CreateTaskRequest request) {
        // ...
    }
    
    @DeleteMapping
    @PreAuthorize("@permissionService.canManageProject(principal, #projectId)")
    public ResponseEntity<Void> deleteProject(@PathVariable Long projectId) {
        // ...
    }
}
```

## Database Schema

The `project_members` table links users to projects with specific permissions:

```sql
CREATE TABLE project_members (
    id BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    permission VARCHAR(20) NOT NULL DEFAULT 'VIEW', -- VIEW, CONTRIBUTOR, MANAGER
    CONSTRAINT fk_pm_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE,
    CONSTRAINT fk_pm_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_project_member UNIQUE (project_id, user_id)
);
```

## Migration Notes

When updating from the old permission model (VIEW/EDIT/ADMIN):
- `VIEW` → `VIEW` (no change)
- `EDIT` → `CONTRIBUTOR` (functionally equivalent)
- `ADMIN` → `MANAGER` (renamed for clarity)
