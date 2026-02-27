# Guardrails and Custom Instructions

This file contains guardrails and custom instructions for the AI assistant when working on this project.

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
