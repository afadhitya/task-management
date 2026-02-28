# Guardrails and Custom Instructions

This file contains guardrails and custom instructions for the AI assistant when working on this project.

---

## System Prompt / Quick Start

**Before starting any task, ALWAYS read these documents in order:**

1. **`/docs/task-management-app-prd-backend.md`** - Product Requirements Document (PRD)
   - Understand the features, requirements, and API specifications

2. **`/docs/clean-architecture.md`** - Architecture Overview
   - Understand the project structure, layers, and coding patterns

3. **`/docs/api-checkpoint.md`** - API Implementation Status
   - Check which APIs are pending, in progress, or done
   - Update status when completing APIs

4. **`/AGENTS.md`** - This file
   - Review guardrails and conventions

**⚡ IMPORTANT:** Do NOT scan the entire codebase initially. Start with the documentation above, then only explore specific code files as needed for the task at hand.

---

## Guardrails

### Security & Safety
- **No destructive operations** without explicit confirmation
- **No git mutations** (commit, push, reset, rebase) unless explicitly asked
- **No file modifications outside the working directory**
- **No execution of untrusted code** without review

### Code Quality
- Follow existing code style and conventions
- Make minimal changes to achieve the goal
- Maintain backward compatibility unless instructed otherwise
- Add/update tests when modifying functionality

### Communication
- Be concise and accurate
- Ask for clarification when requirements are unclear
- Explain reasoning for significant decisions

---

## Custom Instructions

### Project Context
- **Project Type**: Task Management System (Backend API)
- **Language/Framework**: Java 17, Spring Boot 4.0.3, Gradle
- **Architecture**: Clean Architecture (Robert C. Martin) with 4 layers:
  - `domain` - Entities, enums (no framework dependencies)
  - `application` - Use cases, ports (interfaces), DTOs, mappers
  - `adapter` - Controllers (in/web), Persistence adapters (out/persistence)
  - `infrastructure` - Config, security
- **Package Base**: `com.afadhitya.taskmanagement`

### Coding Conventions
- **Naming**: 
  - Use cases: `[Action][Entity]UseCase` interface in `port/in`, `[Action][Entity]UseCaseImpl` in `usecase`
  - Controllers: `[Entity]Controller` with `@RestController`
  - DTOs: `[Action][Entity]Request`, `[Entity]Response`
  - Mappers: MapStruct with `@Mapper(componentModel = "spring")`
- **Formatting**: Standard Java conventions, Lombok for boilerplate
- **Documentation**: No unnecessary comments
- **Comment**: Only add comments for important logic that helps others understand
- **No comments/Javadoc needed** unless explicitly requested by user

### Preferences
- Use constructor injection with `@RequiredArgsConstructor` (Lombok)
- Use `ResponseEntity<T>` for all controller responses
- Return `201 CREATED` for POST, `200 OK` for GET/PUT/PATCH, `204 NO_CONTENT` for DELETE
- Use Bean Validation (`@Valid`) on request DTOs
- **Use immutable objects where possible** - prefer `final` fields, avoid setters
- **Always use Builder pattern** when constructing POJOs - use Lombok's `@Builder` on DTOs and entities

### Special Rules
- **Domain entities have JPA annotations** (pragmatic approach - not pure Clean Architecture)
- **No separate JPA entities** - Domain entities are used directly for persistence
- **MapStruct** is used for DTO/Entity mapping
- **Use MapStruct for updates** - When implementing update/patch operations, use MapStruct's `@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)` with `@MappingTarget` to avoid manual null-checking for each field. Example:
  ```java
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromRequest(UpdateRequest request, @MappingTarget Entity entity);
  ```
- **Use `@With` for path variable overrides** - When a request DTO needs to be updated with a path variable (e.g., `projectId` from `/projects/{projectId}/tasks`), use Lombok's `@With` on the field instead of rebuilding the entire object. Example:
  ```java
  // In DTO
  @With
  Long projectId;
  
  // In Controller
  CreateTaskRequest requestWithProjectId = request.withProjectId(projectId);
  ```
- **Project Member Role Update Permission Rules** - When implementing project member role updates, follow these permission rules:
  - **Workspace OWNER/ADMIN**: Can change any member's role (including MANAGER)
  - **Project MANAGER**: Can change CONTRIBUTOR or VIEW members only, CANNOT change another MANAGER's role
  - **CONTRIBUTOR/VIEW**: No permission to change roles
  - **Last Manager Safeguard**: Always prevent demoting the last manager - at least one MANAGER must remain in every project
- **Controllers paths are inconsistent** - some use `/api/*`, some don't (standardize when touching related code)
- **Use method-level RBAC with `@PreAuthorize`** - For resource-level permission checks (update, delete), use Spring Security method-level annotations with custom security expression beans (e.g., `@labelSecurity`, `@workspaceSecurity`, `@projectSecurity`). Example:
  ```java
  @PreAuthorize("@labelSecurity.canUpdateLabel(#id)")
  @PatchMapping("/labels/{id}")
  public ResponseEntity<LabelResponse> updateLabel(@PathVariable Long id, ...) { ... }
  ```
  - Create security expression beans in `infrastructure/security/[Entity]SecurityExpression.java`
  - Keep use cases focused on business logic, security handled at controller level
  - For create operations that need request body data, permission checks may remain in use case

### Audit Log Implementation Pattern
When implementing audit logging, follow the **Decorator Pattern with Direct Service Calls**:

```
Controller → UseCase Interface → @Primary Decorator → Impl (pure logic)
                                        │
                                        └──▶ AuditLogService (REQUIRES_NEW)
```

**Key Principles:**
1. **Use cases are pure** - No audit code in use case implementations
2. **Decorators handle audit** - Create `@Primary` decorator classes that wrap use cases
3. **Direct service calls** - Call `AuditLogService` directly (not event-driven)
4. **Transaction isolation** - Use `REQUIRES_NEW` for audit log persistence
5. **Diff calculation** - Calculate diffs in decorator before/after calling delegate

**Example Structure:**
```java
@Service
@Primary
@RequiredArgsConstructor
public class AuditedUpdateTaskUseCase implements UpdateTaskUseCase {
    
    private final UpdateTaskUseCaseImpl delegate;  // Pure business logic
    private final AuditLogService auditLogService;
    
    @Override
    @Transactional
    public TaskResponse updateTask(Long id, UpdateTaskRequest request) {
        // 1. Capture before state
        Task oldTask = taskRepository.findById(id).orElseThrow();
        Map<String, Object> diff = calculateDiff(oldTask, request);
        
        // 2. Execute pure business logic
        TaskResponse response = delegate.updateTask(id, request);
        
        // 3. Create audit log (separate transaction)
        if (!diff.isEmpty()) {
            auditLogService.createUpdate(workspaceId, actorId, 
                AuditEntityType.TASK, id, diff);
        }
        
        return response;
    }
}
```

**Location:** Place all audit decorators in `application/usecase/audit/` package.

**Note on AuditContextHolder:** 
`AuditContextHolder` (ThreadLocal) was considered but not used. Decorators fetch workspace/actor info directly from the database. This avoids:
- ThreadLocal complexity and cleanup requirements
- Controller modifications
- Risk of context leaks

The double DB read is acceptable for audit operations (relatively infrequent). 

### Task Checkpoint
- Checked to the file /docs/api-checkpoint, the api that listed on the task checkpoint should be same with the defined in prd doc file 
- After finishing implementing the api, then set to done

---

## Build & Test Instructions

- **No need to run build after implementation** - user will do it themselves unless explicitly requested

### Build Commands
```bash
# Build the project
./gradlew build

# Run tests
./gradlew test
```

### Environment Setup
<!-- Add any specific environment requirements -->


---
