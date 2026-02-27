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
- **Controllers paths are inconsistent** - some use `/api/*`, some don't (standardize when touching related code) 

### Task Checkpoint
- Checked to the file /docs/api-checkpoint, the api that listed on the task checkpoint should be same with the defined in prd doc file 
- After finishing implementing the api, then set to done

---

## Build & Test Instructions

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
