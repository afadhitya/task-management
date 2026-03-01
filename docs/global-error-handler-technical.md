# Technical Documentation
## Global Error Handler Implementation

**Version:** 1.0  
**Date:** March 1, 2026  
**Status:** Draft  
**Related Documents:**
- [Global Error Handler PRD](./global-error-handler-prd.md) - Product requirements
- [Clean Architecture](./clean-architecture.md) - Project structure
- [Task Management App PRD](./task-management-app-prd-backend.md) - Primary PRD

---

## 1. Architecture Overview

### 1.1 Component Diagram

```
HTTP Request
    │
    ▼
┌──────────────────────┐
│   Controller         │  ← Throws exception
└──────────┬───────────┘
           │
           ▼ (exception propagates)
┌──────────────────────┐
│ GlobalException      │  ← @RestControllerAdvice
│ Handler              │  ← Catches exception
│                      │
│ ┌──────────────────┐ │
│ │ ExceptionHandler │ │  ← Maps to error response
│ └────────┬─────────┘ │
└──────────┼───────────┘
           │
           ▼
┌──────────────────────┐
│   ErrorResponse      │  ← DTO
└──────────┬───────────┘
           │
           ▼
┌──────────────────────┐
│   JSON Response      │  ← Client receives
└──────────────────────┘
```

### 1.2 Clean Architecture Placement

```
domain/
  └── exception/           ← Custom exceptions (already exist)

application/
  └── dto/
      └── response/
          └── ErrorResponse.java  ← Error response DTO

infrastructure/
  └── config/
      └── GlobalExceptionHandler.java  ← Exception handler
```

---

## 2. Implementation Components

### 2.1 ErrorResponse DTO

**Location:** `application/dto/response/ErrorResponse.java`

**Purpose:** Immutable data structure for standardized error responses.

```java
package com.afadhitya.taskmanagement.application.dto.response;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
    Instant timestamp,
    int status,
    String error,
    String message,
    Map<String, Object> details,
    String path
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Instant timestamp = Instant.now();
        private int status;
        private String error;
        private String message;
        private Map<String, Object> details;
        private String path;

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder error(String error) {
            this.error = error;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder details(Map<String, Object> details) {
            this.details = details;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public ErrorResponse build() {
            return new ErrorResponse(timestamp, status, error, message, details, path);
        }
    }
}
```

**Key Points:**
- Uses Java Record for immutability
- Builder pattern for flexible construction
- Defaults timestamp to current time

---

### 2.2 GlobalExceptionHandler

**Location:** `infrastructure/config/GlobalExceptionHandler.java`

**Purpose:** Central exception handler using Spring's `@RestControllerAdvice`.

```java
package com.afadhitya.taskmanagement.infrastructure.config;

import com.afadhitya.taskmanagement.application.dto.response.ErrorResponse;
import com.afadhitya.taskmanagement.domain.exception.*;
import com.afadhitya.taskmanagement.domain.feature.LimitType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // === Plan Limit Exceeded ===
    @ExceptionHandler(PlanLimitExceededException.class)
    public ResponseEntity<ErrorResponse> handlePlanLimitExceeded(
            PlanLimitExceededException ex, 
            HttpServletRequest request) {
        
        Map<String, Object> details = new HashMap<>();
        details.put("limitType", ex.getLimitType().getCode());
        details.put("limit", ex.getLimit());
        details.put("used", ex.getCurrentUsage());
        details.put("upgradeTo", getUpgradeRecommendation(ex.getLimitType()));

        ErrorResponse response = ErrorResponse.builder()
            .status(HttpStatus.FORBIDDEN.value())
            .error("PLAN_LIMIT_EXCEEDED")
            .message(ex.getMessage())
            .details(details)
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // === Workspace Access Denied ===
    @ExceptionHandler(WorkspaceAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleWorkspaceAccessDenied(
            WorkspaceAccessDeniedException ex,
            HttpServletRequest request) {

        Map<String, Object> details = new HashMap<>();
        details.put("workspaceId", ex.getWorkspaceId());
        details.put("userId", ex.getUserId());

        ErrorResponse response = ErrorResponse.builder()
            .status(HttpStatus.FORBIDDEN.value())
            .error("WORKSPACE_ACCESS_DENIED")
            .message(ex.getMessage())
            .details(details)
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // === Not Workspace Owner ===
    @ExceptionHandler(NotWorkspaceOwnerException.class)
    public ResponseEntity<ErrorResponse> handleNotWorkspaceOwner(
            NotWorkspaceOwnerException ex,
            HttpServletRequest request) {

        Map<String, Object> details = new HashMap<>();
        details.put("workspaceId", ex.getWorkspaceId());
        details.put("userId", ex.getUserId());

        ErrorResponse response = ErrorResponse.builder()
            .status(HttpStatus.FORBIDDEN.value())
            .error("NOT_WORKSPACE_OWNER")
            .message(ex.getMessage())
            .details(details)
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // === Invalid Token ===
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(
            InvalidTokenException ex,
            HttpServletRequest request) {

        ErrorResponse response = ErrorResponse.builder()
            .status(HttpStatus.UNAUTHORIZED.value())
            .error("INVALID_TOKEN")
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    // === Validation Errors (Bean Validation) ===
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, Object> details = new HashMap<>();
        Map<String, String> fieldErrors = new HashMap<>();
        
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        });
        
        details.put("fieldErrors", fieldErrors);

        ErrorResponse response = ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("VALIDATION_ERROR")
            .message("Request validation failed")
            .details(details)
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // === Illegal Argument (Bad Request) ===
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        ErrorResponse response = ErrorResponse.builder()
            .status(HttpStatus.BAD_REQUEST.value())
            .error("BAD_REQUEST")
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // === Access Denied (Spring Security) ===
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        ErrorResponse response = ErrorResponse.builder()
            .status(HttpStatus.FORBIDDEN.value())
            .error("ACCESS_DENIED")
            .message("You do not have permission to perform this action")
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // === Generic Exception (Fallback) ===
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unhandled exception occurred at path: {}", request.getRequestURI(), ex);

        ErrorResponse response = ErrorResponse.builder()
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("INTERNAL_ERROR")
            .message("An unexpected error occurred")
            .path(request.getRequestURI())
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private String getUpgradeRecommendation(LimitType limitType) {
        return switch (limitType) {
            case MAX_PROJECTS -> "TEAM";
            case MAX_MEMBERS -> "TEAM";
            case MAX_STORAGE_MB -> "TEAM";
            default -> "TEAM";
        };
    }
}
```

**Key Points:**
- Uses `@RestControllerAdvice` to handle exceptions globally
- Each handler returns `ResponseEntity<ErrorResponse>`
- Logs unhandled exceptions with full stack traces
- Generic handler catches all uncaught exceptions

---

## 3. Required Exception Updates

### 3.1 Update Existing Exceptions

Some existing exceptions need to expose additional fields for the error handler.

**Location:** `domain/exception/WorkspaceAccessDeniedException.java`

```java
package com.afadhitya.taskmanagement.domain.exception;

import lombok.Getter;

@Getter
public class WorkspaceAccessDeniedException extends RuntimeException {
    private final Long workspaceId;
    private final Long userId;

    public WorkspaceAccessDeniedException(String message) {
        super(message);
        this.workspaceId = null;
        this.userId = null;
    }

    public WorkspaceAccessDeniedException(Long workspaceId, Long userId) {
        super(String.format("Access denied to workspace %d for user %d", workspaceId, userId));
        this.workspaceId = workspaceId;
        this.userId = userId;
    }
}
```

**Location:** `domain/exception/NotWorkspaceOwnerException.java`

```java
package com.afadhitya.taskmanagement.domain.exception;

import lombok.Getter;

@Getter
public class NotWorkspaceOwnerException extends RuntimeException {
    private final Long workspaceId;
    private final Long userId;

    public NotWorkspaceOwnerException(String message) {
        super(message);
        this.workspaceId = null;
        this.userId = null;
    }

    public NotWorkspaceOwnerException(Long workspaceId, Long userId) {
        super(String.format("User %d is not the owner of workspace %d", userId, workspaceId));
        this.workspaceId = workspaceId;
        this.userId = userId;
    }
}
```

---

## 4. Error Mapping Reference

### 4.1 Exception to HTTP Status Mapping

| Exception Class | HTTP Status | Error Code |
|-----------------|-------------|------------|
| `PlanLimitExceededException` | 403 FORBIDDEN | PLAN_LIMIT_EXCEEDED |
| `WorkspaceAccessDeniedException` | 403 FORBIDDEN | WORKSPACE_ACCESS_DENIED |
| `NotWorkspaceOwnerException` | 403 FORBIDDEN | NOT_WORKSPACE_OWNER |
| `InvalidTokenException` | 401 UNAUTHORIZED | INVALID_TOKEN |
| `MethodArgumentNotValidException` | 400 BAD_REQUEST | VALIDATION_ERROR |
| `IllegalArgumentException` | 400 BAD_REQUEST | BAD_REQUEST |
| `AccessDeniedException` | 403 FORBIDDEN | ACCESS_DENIED |
| `Exception` (fallback) | 500 INTERNAL_ERROR | INTERNAL_ERROR |

### 4.2 Error Response Details by Type

**PLAN_LIMIT_EXCEEDED:**
```json
{
  "details": {
    "limitType": "max_projects",
    "limit": 3,
    "used": 3,
    "upgradeTo": "TEAM"
  }
}
```

**VALIDATION_ERROR:**
```json
{
  "details": {
    "fieldErrors": {
      "fieldName": "error message"
    }
  }
}
```

**WORKSPACE_ACCESS_DENIED / NOT_WORKSPACE_OWNER:**
```json
{
  "details": {
    "workspaceId": 123,
    "userId": 456
  }
}
```

---

## 5. Testing Strategy

### 5.1 Unit Tests

**Location:** `src/test/java/com/afadhitya/taskmanagement/infrastructure/config/GlobalExceptionHandlerTest.java`

```java
package com.afadhitya.taskmanagement.infrastructure.config;

import com.afadhitya.taskmanagement.application.dto.response.ErrorResponse;
import com.afadhitya.taskmanagement.domain.exception.*;
import com.afadhitya.taskmanagement.domain.feature.LimitType;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest request;

    @Test
    void handlePlanLimitExceeded_shouldReturnForbiddenWithDetails() {
        // Given
        PlanLimitExceededException ex = new PlanLimitExceededException(
            LimitType.MAX_PROJECTS, 3, 3
        );
        when(request.getRequestURI()).thenReturn("/workspaces/123/projects");

        // When
        ResponseEntity<ErrorResponse> response = handler.handlePlanLimitExceeded(ex, request);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("PLAN_LIMIT_EXCEEDED", response.getBody().error());
        assertEquals("TEAM", response.getBody().details().get("upgradeTo"));
        assertEquals("max_projects", response.getBody().details().get("limitType"));
    }

    @Test
    void handleWorkspaceAccessDenied_shouldReturnForbiddenWithIds() {
        // Given
        WorkspaceAccessDeniedException ex = new WorkspaceAccessDeniedException(123L, 456L);
        when(request.getRequestURI()).thenReturn("/workspaces/123");

        // When
        ResponseEntity<ErrorResponse> response = handler.handleWorkspaceAccessDenied(ex, request);

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("WORKSPACE_ACCESS_DENIED", response.getBody().error());
        assertEquals(123L, response.getBody().details().get("workspaceId"));
        assertEquals(456L, response.getBody().details().get("userId"));
    }

    @Test
    void handleInvalidToken_shouldReturnUnauthorized() {
        // Given
        InvalidTokenException ex = new InvalidTokenException("Token has expired");
        when(request.getRequestURI()).thenReturn("/api/auth/refresh-token");

        // When
        ResponseEntity<ErrorResponse> response = handler.handleInvalidToken(ex, request);

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_TOKEN", response.getBody().error());
        assertEquals("Token has expired", response.getBody().message());
    }

    @Test
    void handleGenericException_shouldReturnInternalError() {
        // Given
        Exception ex = new RuntimeException("Something went wrong");
        when(request.getRequestURI()).thenReturn("/api/tasks/123");

        // When
        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex, request);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_ERROR", response.getBody().error());
        assertEquals("An unexpected error occurred", response.getBody().message());
    }
}
```

---

## 6. Deployment Considerations

### 6.1 Prerequisites

- No database migrations required
- No configuration changes needed
- Lombok annotation processor must be configured

### 6.2 Deployment Steps

1. Create `ErrorResponse.java` in `application/dto/response/`
2. Create `GlobalExceptionHandler.java` in `infrastructure/config/`
3. Update existing exception classes if needed
4. Run tests: `./gradlew test`
5. Deploy to environment

### 6.3 Rollback Plan

- Remove `GlobalExceptionHandler.java` to revert to Spring's default error handling
- Remove `ErrorResponse.java` if no longer needed

---

## 7. Monitoring and Observability

### 7.1 Logging

- **INFO**: All handled exceptions (4xx errors)
- **ERROR**: Unhandled exceptions (5xx errors) with full stack trace
- **WARN**: Authentication/authorization failures

### 7.2 Metrics to Track

- Error rate by error code
- HTTP status code distribution
- Average error response time
- Most common validation errors

---

## 8. Security Considerations

1. **No stack traces in production** - Generic handler does not expose stack traces
2. **Sensitive data filtering** - Error messages should never include passwords or tokens
3. **Internal details hidden** - Generic error message for 500 errors
4. **Path exposure** - Request path is included for debugging but does not expose sensitive data

---

## 9. Future Enhancements

### 9.1 Potential Improvements

1. **Multi-language support**
   ```java
   .message(messageSource.getMessage(errorCode, null, locale))
   ```

2. **Request ID tracking**
   ```java
   .requestId(MDC.get("requestId"))
   ```

3. **Error documentation links**
   ```json
   {
     "helpUrl": "https://docs.example.com/errors/PLAN_LIMIT_EXCEEDED"
   }
   ```

4. **Sentry integration**
   ```java
   Sentry.captureException(ex);
   ```

---

*Document Owner: Engineering Team*  
*Last Updated: March 1, 2026*
