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
<!-- Add specific context about this project -->
- **Project Type**: Task Management System
- **Language/Framework**: Java (Gradle-based)
- **Architecture**: 

### Coding Conventions
<!-- Define your preferred coding styles -->
- **Naming**: 
- **Formatting**: 
- **Documentation**: 
- **Comment**: No need to add any comment if not necessary, except it is important and will help others

### Preferences
<!-- Add any personal/team preferences -->
- 

### Special Rules
<!-- Any specific rules for this project -->
- 

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
