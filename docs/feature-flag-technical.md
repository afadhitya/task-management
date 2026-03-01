# Technical Documentation
## Feature Flag & Plan-Based Access Control System

**Version:** 1.0  
**Date:** February 28, 2026  
**Status:** Draft  
**Related Documents:** 
- [Feature Flag PRD](./feature-flag-prd.md)
- [Clean Architecture](./clean-architecture.md)

---

## 1. Architecture Overview

### 1.1 Design Principles

1. **Zero Resource Waste**: Disabled features must not perform any DB reads/writes
2. **Clean Separation**: Feature logic isolated from business logic
3. **Extensibility**: New features can be added without modifying dispatcher
4. **Performance**: Cached feature checks with minimal overhead
5. **Testability**: Each component testable in isolation

### 1.2 High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           CONTROLLER LAYER                                   │
│                                                                              │
│   TaskController ──► UpdateTaskUseCase (interface)                           │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
                                       │
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                     FEATURE DISPATCHER LAYER                                 │
│                                                                              │
│   ┌─────────────────────────────────────────────────────────────────────┐   │
│   │              TaskFeatureDispatcher (@Primary)                        │   │
│   │                                                                      │   │
│   │   ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌───────────┐  │   │
│   │   │   VALIDATE  │─▶│    BEFORE   │─▶│   DELEGATE  │─▶│   AFTER   │  │   │
│   │   └─────────────┘  └─────────────┘  └─────────────┘  └───────────┘  │   │
│   │          │                 │                              │          │   │
│   │          ▼                 ▼                              ▼          │   │
│   │   ┌─────────────────────────────────────────────────────────────┐   │   │
│   │   │              FeatureHandler Chain                            │   │   │
│   │   │  ┌──────────┐  ┌──────────────┐  ┌──────────────────────┐  │   │   │
│   │   │  │  Audit   │  │ Notification │  │   ProjectLimit       │  │   │   │
│   │   │  │  Handler │  │   Handler    │  │     Handler          │  │   │   │
│   │   │  └──────────┘  └──────────────┘  └──────────────────────┘  │   │   │
│   │   └─────────────────────────────────────────────────────────────┘   │   │
│   │                                                                      │   │
│   └─────────────────────────────────────────────────────────────────────┘   │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
                                       │
                                       ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                         BUSINESS LOGIC LAYER                                 │
│                                                                              │
│   UpdateTaskUseCaseImpl ──► Pure business logic, no feature awareness        │
│                                                                              │
└─────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Domain Layer

### 2.1 Feature Enum

```java
package com.afadhitya.taskmanagement.domain.feature;

public enum Feature {
    AUDIT_LOG("audit_log", FeatureCategory.SECURITY, FeatureTiming.POST),
    NOTIFICATIONS("notifications", FeatureCategory.COLLABORATION, FeatureTiming.POST),
    ADVANCED_SEARCH("advanced_search", FeatureCategory.PRODUCTIVITY, FeatureTiming.ASYNC),
    WEBHOOKS("webhooks", FeatureCategory.INTEGRATION, FeatureTiming.ASYNC),
    ATTACHMENTS("attachments", FeatureCategory.COLLABORATION, FeatureTiming.VALIDATE),
    BULK_OPERATIONS("bulk_operations", FeatureCategory.PRODUCTIVITY, FeatureTiming.VALIDATE),
    PROJECT_LIMITS("project_limits", FeatureCategory.LIMITS, FeatureTiming.VALIDATE),
    MEMBER_LIMITS("member_limits", FeatureCategory.LIMITS, FeatureTiming.VALIDATE),
    STORAGE_LIMITS("storage_limits", FeatureCategory.LIMITS, FeatureTiming.VALIDATE);

    private final String code;
    private final FeatureCategory category;
    private final FeatureTiming defaultTiming;

    Feature(String code, FeatureCategory category, FeatureTiming defaultTiming) {
        this.code = code;
        this.category = category;
        this.defaultTiming = defaultTiming;
    }

    public String getCode() { return code; }
    public FeatureCategory getCategory() { return category; }
    public FeatureTiming getDefaultTiming() { return defaultTiming; }
}

public enum FeatureCategory {
    SECURITY, COLLABORATION, PRODUCTIVITY, INTEGRATION, LIMITS
}

public enum FeatureTiming {
    VALIDATE,  // Before execution - can block
    PRE,       // Before execution - capture state
    POST,      // After execution - react to result
    ASYNC      // After execution - fire and forget
}
```

### 2.2 Limit Type Enum

```java
package com.afadhitya.taskmanagement.domain.feature;

public enum LimitType {
    MAX_PROJECTS("max_projects", "Maximum projects per workspace"),
    MAX_MEMBERS("max_members", "Maximum members per workspace"),
    MAX_STORAGE_MB("max_storage_mb", "Maximum storage in MB"),
    MAX_TASKS_PER_PROJECT("max_tasks_per_project", "Maximum tasks per project");

    private final String code;
    private final String description;

    LimitType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() { return code; }
    public String getDescription() { return description; }
}
```

### 2.3 Plan Limit Exceeded Exception

```java
package com.afadhitya.taskmanagement.domain.exception;

import com.afadhitya.taskmanagement.domain.feature.LimitType;
import lombok.Getter;

@Getter
public class PlanLimitExceededException extends RuntimeException {
    private final LimitType limitType;
    private final int currentUsage;
    private final int limit;

    public PlanLimitExceededException(LimitType limitType, int currentUsage, int limit) {
        super(String.format("Plan limit exceeded: %s (used: %d, limit: %d)", 
            limitType.getCode(), currentUsage, limit));
        this.limitType = limitType;
        this.currentUsage = currentUsage;
        this.limit = limit;
    }
}
```

---

## 3. Application Layer

### 3.1 Feature Handler Interface

```java
package com.afadhitya.taskmanagement.application.port.feature;

import com.afadhitya.taskmanagement.domain.feature.Feature;

/**
 * Pluggable handler for feature-specific logic.
 * Each feature (audit, notification, limits) implements this interface.
 */
public interface FeatureHandler<R, T> {
    
    /**
     * The feature this handler manages
     */
    Feature getFeature();
    
    /**
     * VALIDATE phase: Check limits before execution.
     * Throw PlanLimitExceededException to block operation.
     */
    default void validate(FeatureContext context, R request) {
        // Default: no validation
    }
    
    /**
     * PRE phase: Capture state before execution.
     * Only called if feature is enabled.
     */
    default void before(FeatureContext context, R request) {
        // Default: no pre-processing
    }
    
    /**
     * POST phase: React to successful execution.
     * Only called if feature is enabled.
     */
    default void after(FeatureContext context, T result) {
        // Default: no post-processing
    }
    
    /**
     * ERROR phase: React to failed execution.
     * Called regardless of feature enablement for cleanup.
     */
    default void onError(FeatureContext context, R request, Exception error) {
        // Default: no error handling
    }
    
    /**
     * ASYNC phase: Fire-and-forget operations.
     * Called in separate thread via @Async.
     */
    default void async(FeatureContext context, T result) {
        // Default: no async processing
    }
}
```

### 3.2 Feature Context

```java
package com.afadhitya.taskmanagement.application.port.feature;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * Shared context across all handlers in a single operation.
 * Allows handlers to share data (e.g., audit needs notification data).
 */
@Getter
public class FeatureContext {
    private final Long workspaceId;
    private final Long actorId;
    private final Map<String, Object> attributes = new HashMap<>();
    
    @Setter
    private boolean executionFailed = false;

    public FeatureContext(Long workspaceId, Long actorId) {
        this.workspaceId = workspaceId;
        this.actorId = actorId;
    }

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }

    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }
}
```

### 3.3 Feature Toggle Port (Output Port)

```java
package com.afadhitya.taskmanagement.application.port.out.feature;

import com.afadhitya.taskmanagement.domain.feature.Feature;
import com.afadhitya.taskmanagement.domain.feature.LimitType;

public interface FeatureTogglePort {
    
    /**
     * Check if a feature is enabled for a workspace
     */
    boolean isEnabled(Long workspaceId, Feature feature);
    
    /**
     * Get limit value for a workspace
     */
    int getLimit(Long workspaceId, LimitType limitType);
    
    /**
     * Invalidate cache for a workspace (when plan changes)
     */
    void invalidateCache(Long workspaceId);
}
```

---

## 4. Concrete Handlers

### 4.1 Task Audit Feature Handler

```java
package com.afadhitya.taskmanagement.application.usecase.feature.handler;

import com.afadhitya.taskmanagement.application.dto.request.UpdateTaskRequest;
import com.afadhitya.taskmanagement.application.dto.response.TaskResponse;
import com.afadhitya.taskmanagement.application.port.feature.FeatureContext;
import com.afadhitya.taskmanagement.application.port.feature.FeatureHandler;
import com.afadhitya.taskmanagement.application.port.out.task.TaskPersistencePort;
import com.afadhitya.taskmanagement.application.service.AuditLogService;
import com.afadhitya.taskmanagement.domain.entity.Task;
import com.afadhitya.taskmanagement.domain.enums.AuditAction;
import com.afadhitya.taskmanagement.domain.enums.AuditEntityType;
import com.afadhitya.taskmanagement.domain.feature.Feature;
import com.afadhitya.taskmanagement.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TaskAuditFeatureHandler implements FeatureHandler<UpdateTaskRequest, TaskResponse> {
    
    private final TaskPersistencePort taskPersistencePort;
    private final AuditLogService auditLogService;

    @Override
    public Feature getFeature() {
        return Feature.AUDIT_LOG;
    }

    @Override
    public void before(FeatureContext context, UpdateTaskRequest request) {
        Long taskId = context.getAttribute("taskId");
        
        // Only fetch old state if audit is enabled - SAVES DB READ
        Task oldTask = taskPersistencePort.findById(taskId).orElse(null);
        context.setAttribute("audit.oldTask", oldTask);
        
        if (oldTask != null) {
            context.setAttribute("workspaceId", oldTask.getProject().getWorkspace().getId());
        }
    }

    @Override
    public void after(FeatureContext context, TaskResponse result) {
        Task oldTask = context.getAttribute("audit.oldTask");
        if (oldTask == null) return;

        Map<String, Object> diff = calculateDiff(oldTask, result);
        if (!diff.isEmpty()) {
            Long workspaceId = context.getAttribute("workspaceId");
            AuditAction action = diff.containsKey("status") 
                ? AuditAction.STATUS_CHANGE 
                : AuditAction.UPDATE;

            auditLogService.create(
                workspaceId,
                SecurityUtils.getCurrentUserId(),
                AuditEntityType.TASK,
                result.id(),
                action,
                diff
            );
        }
    }

    private Map<String, Object> calculateDiff(Task old, TaskResponse current) {
        Map<String, Object> diff = new HashMap<>();
        
        if (current.title() != null && !current.title().equals(old.getTitle())) {
            diff.put("title", Map.of("old", old.getTitle(), "new", current.title()));
        }
        if (current.description() != null && !current.description().equals(old.getDescription())) {
            diff.put("description", Map.of("old", old.getDescription(), "new", current.description()));
        }
        if (current.status() != null && current.status() != old.getStatus()) {
            diff.put("status", Map.of("old", old.getStatus().name(), "new", current.status().name()));
        }
        if (current.priority() != null && current.priority() != old.getPriority()) {
            diff.put("priority", Map.of("old", old.getPriority().name(), "new", current.priority().name()));
        }
        if (current.dueDate() != null && !current.dueDate().equals(old.getDueDate())) {
            diff.put("dueDate", Map.of("old", old.getDueDate(), "new", current.dueDate()));
        }
        if (current.assigneeIds() != null && !current.assigneeIds().equals(old.getAssigneeIds())) {
            diff.put("assigneeIds", Map.of("old", old.getAssigneeIds(), "new", current.assigneeIds()));
        }
        
        return diff;
    }
}
```

### 4.2 Project Limit Feature Handler

```java
package com.afadhitya.taskmanagement.application.usecase.feature.handler;

import com.afadhitya.taskmanagement.application.dto.request.CreateProjectRequest;
import com.afadhitya.taskmanagement.application.dto.response.ProjectResponse;
import com.afadhitya.taskmanagement.application.port.feature.FeatureContext;
import com.afadhitya.taskmanagement.application.port.feature.FeatureHandler;
import com.afadhitya.taskmanagement.application.port.out.feature.FeatureTogglePort;
import com.afadhitya.taskmanagement.application.port.out.workspace.WorkspacePersistencePort;
import com.afadhitya.taskmanagement.domain.exception.PlanLimitExceededException;
import com.afadhitya.taskmanagement.domain.feature.Feature;
import com.afadhitya.taskmanagement.domain.feature.LimitType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjectLimitFeatureHandler implements FeatureHandler<CreateProjectRequest, ProjectResponse> {
    
    private final WorkspacePersistencePort workspacePersistencePort;
    private final FeatureTogglePort featureTogglePort;

    @Override
    public Feature getFeature() {
        return Feature.PROJECT_LIMITS;
    }

    @Override
    public void validate(FeatureContext context, CreateProjectRequest request) {
        Long workspaceId = request.workspaceId();
        
        int currentProjectCount = workspacePersistencePort.countProjects(workspaceId);
        int maxAllowed = featureTogglePort.getLimit(workspaceId, LimitType.MAX_PROJECTS);
        
        // -1 means unlimited
        if (maxAllowed >= 0 && currentProjectCount >= maxAllowed) {
            throw new PlanLimitExceededException(
                LimitType.MAX_PROJECTS,
                currentProjectCount,
                maxAllowed
            );
        }
    }
}
```

---

## 5. Feature Dispatcher

### 5.1 Task Feature Dispatcher

```java
package com.afadhitya.taskmanagement.application.usecase.feature;

import com.afadhitya.taskmanagement.application.dto.request.UpdateTaskRequest;
import com.afadhitya.taskmanagement.application.dto.response.TaskResponse;
import com.afadhitya.taskmanagement.application.port.feature.FeatureContext;
import com.afadhitya.taskmanagement.application.port.feature.FeatureHandler;
import com.afadhitya.taskmanagement.application.port.in.task.UpdateTaskUseCase;
import com.afadhitya.taskmanagement.application.port.out.feature.FeatureTogglePort;
import com.afadhitya.taskmanagement.application.usecase.task.UpdateTaskUseCaseImpl;
import com.afadhitya.taskmanagement.domain.feature.Feature;
import com.afadhitya.taskmanagement.infrastructure.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class TaskFeatureDispatcher implements UpdateTaskUseCase {
    
    private final UpdateTaskUseCaseImpl delegate;
    private final FeatureTogglePort featureToggle;
    private final List<FeatureHandler<UpdateTaskRequest, TaskResponse>> handlers;
    
    private final Map<Feature, FeatureHandler<UpdateTaskRequest, TaskResponse>> handlerMap;

    public TaskFeatureDispatcher(
            UpdateTaskUseCaseImpl delegate,
            FeatureTogglePort featureToggle,
            List<FeatureHandler<UpdateTaskRequest, TaskResponse>> handlers) {
        this.delegate = delegate;
        this.featureToggle = featureToggle;
        this.handlers = handlers;
        this.handlerMap = new EnumMap<>(Feature.class);
        handlers.forEach(h -> handlerMap.put(h.getFeature(), h));
    }

    @Override
    @Transactional
    public TaskResponse updateTask(Long id, UpdateTaskRequest request) {
        Long workspaceId = request.workspaceId();
        Long actorId = SecurityUtils.getCurrentUserId();
        
        FeatureContext context = new FeatureContext(workspaceId, actorId);
        context.setAttribute("taskId", id);
        
        // Check which features are enabled BEFORE any expensive operations
        Map<Feature, Boolean> enabledFeatures = checkFeatures(workspaceId, 
            Feature.AUDIT_LOG, Feature.NOTIFICATIONS, Feature.ADVANCED_SEARCH);
        
        try {
            // PHASE 1: VALIDATE (limit checks)
            // No validate handlers for update task currently
            
            // PHASE 2: PRE (capture state for enabled features only)
            if (enabledFeatures.get(Feature.AUDIT_LOG)) {
                executePre(Feature.AUDIT_LOG, context, request);
            }
            // If audit disabled: ZERO DB reads for old task state!
            
            // PHASE 3: CORE BUSINESS LOGIC (pure, no feature awareness)
            TaskResponse result = delegate.updateTask(id, request);
            
            // PHASE 4: POST (react to result for enabled features only)
            if (enabledFeatures.get(Feature.AUDIT_LOG)) {
                executePost(Feature.AUDIT_LOG, context, result);
            }
            if (enabledFeatures.get(Feature.NOTIFICATIONS)) {
                executePost(Feature.NOTIFICATIONS, context, result);
            }
            
            // PHASE 5: ASYNC (fire and forget)
            if (enabledFeatures.get(Feature.ADVANCED_SEARCH)) {
                executeAsync(Feature.ADVANCED_SEARCH, context, result);
            }
            
            return result;
            
        } catch (Exception e) {
            context.setExecutionFailed(true);
            executeOnError(context, request, e);
            throw e;
        }
    }
    
    private Map<Feature, Boolean> checkFeatures(Long workspaceId, Feature... features) {
        Map<Feature, Boolean> result = new EnumMap<>(Feature.class);
        for (Feature feature : features) {
            result.put(feature, featureToggle.isEnabled(workspaceId, feature));
        }
        return result;
    }
    
    private void executePre(Feature feature, FeatureContext context, UpdateTaskRequest request) {
        FeatureHandler<UpdateTaskRequest, TaskResponse> handler = handlerMap.get(feature);
        if (handler != null) {
            log.debug("Executing PRE for feature: {}", feature);
            handler.before(context, request);
        }
    }
    
    private void executePost(Feature feature, FeatureContext context, TaskResponse result) {
        FeatureHandler<UpdateTaskRequest, TaskResponse> handler = handlerMap.get(feature);
        if (handler != null) {
            log.debug("Executing POST for feature: {}", feature);
            handler.after(context, result);
        }
    }
    
    private void executeAsync(Feature feature, FeatureContext context, TaskResponse result) {
        FeatureHandler<UpdateTaskRequest, TaskResponse> handler = handlerMap.get(feature);
        if (handler != null) {
            log.debug("Executing ASYNC for feature: {}", feature);
            try {
                handler.async(context, result);
            } catch (Exception e) {
                log.error("Async handler failed for feature: {}", feature, e);
            }
        }
    }
    
    private void executeOnError(FeatureContext context, UpdateTaskRequest request, Exception e) {
        handlers.forEach(handler -> {
            try {
                handler.onError(context, request, e);
            } catch (Exception suppressed) {
                log.error("Error handler failed for feature: {}", handler.getFeature(), suppressed);
            }
        });
    }
}
```

---

## 6. Adapter Layer

### 6.1 Feature Toggle Adapter

```java
package com.afadhitya.taskmanagement.adapter.out.feature;

import com.afadhitya.taskmanagement.application.port.out.feature.FeatureTogglePort;
import com.afadhitya.taskmanagement.domain.feature.Feature;
import com.afadhitya.taskmanagement.domain.feature.LimitType;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FeatureToggleAdapter implements FeatureTogglePort {
    
    private final PlanConfigurationRepository planConfigRepository;
    private final WorkspaceRepository workspaceRepository;

    @Override
    @Cacheable(value = "workspaceFeatures", key = "#workspaceId + ':' + #feature.code()")
    public boolean isEnabled(Long workspaceId, Feature feature) {
        Long planConfigId = workspaceRepository.findPlanConfigurationIdById(workspaceId);
        return planConfigRepository.isFeatureEnabled(planConfigId, feature);
    }

    @Override
    @Cacheable(value = "workspaceLimits", key = "#workspaceId + ':' + #limitType.code()")
    public int getLimit(Long workspaceId, LimitType limitType) {
        Long planConfigId = workspaceRepository.findPlanConfigurationIdById(workspaceId);
        return planConfigRepository.getLimit(planConfigId, limitType);
    }

    @Override
    @CacheEvict(value = {"workspaceFeatures", "workspaceLimits"}, key = "#workspaceId + ':*'")
    public void invalidateCache(Long workspaceId) {
        // Cache eviction handled by annotation
    }
}
```

---

## 7. Database Schema

### 7.1 Migration Script

```sql
-- ============================================
-- V10__Create_feature_flag_system.sql
-- ============================================

-- Features catalog (seeded with available features)
CREATE TABLE features (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    default_timing VARCHAR(20) NOT NULL,
    is_system BOOLEAN DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Plan configurations (replaces simple enum)
CREATE TABLE plan_configurations (
    id BIGSERIAL PRIMARY KEY,
    plan_tier VARCHAR(20) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT true,
    is_default BOOLEAN DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_plan_tier_active UNIQUE NULLS NOT DISTINCT (plan_tier, is_active) 
        WHERE is_active = true
);

-- Feature enablement per plan (the actual flags)
CREATE TABLE plan_features (
    id BIGSERIAL PRIMARY KEY,
    plan_configuration_id BIGINT NOT NULL REFERENCES plan_configurations(id) ON DELETE CASCADE,
    feature_id BIGINT NOT NULL REFERENCES features(id) ON DELETE CASCADE,
    is_enabled BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_plan_feature UNIQUE (plan_configuration_id, feature_id)
);

-- Limits per plan
CREATE TABLE plan_limits (
    id BIGSERIAL PRIMARY KEY,
    plan_configuration_id BIGINT NOT NULL REFERENCES plan_configurations(id) ON DELETE CASCADE,
    limit_type VARCHAR(50) NOT NULL,
    limit_value INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_plan_limit UNIQUE (plan_configuration_id, limit_type)
);

-- Update workspace to reference plan configuration
ALTER TABLE workspaces 
    ADD COLUMN plan_configuration_id BIGINT REFERENCES plan_configurations(id);

-- Create index for performance
CREATE INDEX idx_plan_features_config_id ON plan_features(plan_configuration_id);
CREATE INDEX idx_plan_limits_config_id ON plan_limits(plan_configuration_id);
CREATE INDEX idx_workspaces_plan_config_id ON workspaces(plan_configuration_id);

-- ============================================
-- Seed Data
-- ============================================

-- Insert features
INSERT INTO features (code, name, description, category, default_timing, is_system) VALUES
    ('audit_log', 'Audit Log', 'Track all changes in workspace', 'SECURITY', 'POST', true),
    ('notifications', 'Notifications', 'Email and push notifications', 'COLLABORATION', 'POST', true),
    ('advanced_search', 'Advanced Search', 'Full-text search indexing', 'PRODUCTIVITY', 'ASYNC', true),
    ('webhooks', 'Webhooks', 'External system integrations', 'INTEGRATION', 'ASYNC', true),
    ('attachments', 'Attachments', 'File upload capability', 'COLLABORATION', 'VALIDATE', true),
    ('bulk_operations', 'Bulk Operations', 'Bulk edit tasks', 'PRODUCTIVITY', 'VALIDATE', true),
    ('project_limits', 'Project Limits', 'Limit number of projects', 'LIMITS', 'VALIDATE', true),
    ('member_limits', 'Member Limits', 'Limit workspace members', 'LIMITS', 'VALIDATE', true),
    ('storage_limits', 'Storage Limits', 'Limit storage usage', 'LIMITS', 'VALIDATE', true);

-- Insert plan configurations
INSERT INTO plan_configurations (plan_tier, name, description, is_active, is_default) VALUES
    ('FREE', 'Free Plan', 'Basic features for individuals', true, true),
    ('TEAM', 'Team Plan', 'Advanced features for teams', true, false),
    ('ENTERPRISE', 'Enterprise Plan', 'Full feature set', true, false);

-- Enable features for FREE plan
INSERT INTO plan_features (plan_configuration_id, feature_id, is_enabled)
SELECT pc.id, f.id, f.code IN ('notifications', 'attachments', 'project_limits', 'member_limits', 'storage_limits')
FROM plan_configurations pc
CROSS JOIN features f
WHERE pc.plan_tier = 'FREE';

-- Enable features for TEAM plan
INSERT INTO plan_features (plan_configuration_id, feature_id, is_enabled)
SELECT pc.id, f.id, f.code NOT IN ('audit_log', 'webhooks')
FROM plan_configurations pc
CROSS JOIN features f
WHERE pc.plan_tier = 'TEAM';

-- Enable features for ENTERPRISE plan
INSERT INTO plan_features (plan_configuration_id, feature_id, is_enabled)
SELECT pc.id, f.id, true
FROM plan_configurations pc
CROSS JOIN features f
WHERE pc.plan_tier = 'ENTERPRISE';

-- Set limits for FREE plan
INSERT INTO plan_limits (plan_configuration_id, limit_type, limit_value)
SELECT id, 'MAX_PROJECTS', 3 FROM plan_configurations WHERE plan_tier = 'FREE'
UNION ALL
SELECT id, 'MAX_MEMBERS', 5 FROM plan_configurations WHERE plan_tier = 'FREE'
UNION ALL
SELECT id, 'MAX_STORAGE_MB', 100 FROM plan_configurations WHERE plan_tier = 'FREE';

-- Set limits for TEAM plan
INSERT INTO plan_limits (plan_configuration_id, limit_type, limit_value)
SELECT id, 'MAX_PROJECTS', 50 FROM plan_configurations WHERE plan_tier = 'TEAM'
UNION ALL
SELECT id, 'MAX_MEMBERS', 50 FROM plan_configurations WHERE plan_tier = 'TEAM'
UNION ALL
SELECT id, 'MAX_STORAGE_MB', 10240 FROM plan_configurations WHERE plan_tier = 'TEAM';

-- Set limits for ENTERPRISE plan (-1 = unlimited)
INSERT INTO plan_limits (plan_configuration_id, limit_type, limit_value)
SELECT id, 'MAX_PROJECTS', -1 FROM plan_configurations WHERE plan_tier = 'ENTERPRISE'
UNION ALL
SELECT id, 'MAX_MEMBERS', -1 FROM plan_configurations WHERE plan_tier = 'ENTERPRISE'
UNION ALL
SELECT id, 'MAX_STORAGE_MB', -1 FROM plan_configurations WHERE plan_tier = 'ENTERPRISE';

-- Migrate existing workspaces
UPDATE workspaces 
SET plan_configuration_id = (
    SELECT id FROM plan_configurations 
    WHERE plan_tier = workspaces.plan_tier AND is_active = true
);
```

---

## 8. Configuration

### 8.1 Cache Configuration

```java
package com.afadhitya.taskmanagement.infrastructure.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(10000));
        return cacheManager;
    }
}
```

### 8.2 Async Configuration

```java
package com.afadhitya.taskmanagement.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.context.annotation.Bean;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
    
    @Bean(name = "featureAsyncExecutor")
    public Executor featureAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("feature-async-");
        executor.initialize();
        return executor;
    }
}
```

---

## 9. Migration Guide

### 9.1 From Existing Audit Decorators

Current state:
```java
@Service
@Primary
public class AuditedTaskUseCases {
    // Inner classes for each operation
}
```

Migration steps:

1. **Create Feature enum** in domain layer
2. **Create FeatureHandler interface** in application port
3. **Create FeatureContext** for shared state
4. **Create FeatureTogglePort** interface
5. **Implement FeatureToggleAdapter** in adapter layer
6. **Create TaskAuditFeatureHandler** (moves logic from AuditedTaskUseCases)
7. **Create TaskFeatureDispatcher** (replaces AuditedTaskUseCases)
8. **Add database migration** for plan configuration tables
9. **Remove AuditedTaskUseCases** (after testing)

### 9.2 Adding New Feature

1. Add feature to `Feature` enum
2. Create handler implementing `FeatureHandler`
3. Register handler as Spring `@Component`
4. Add to plan configuration in database
5. Update dispatcher to check the feature

---

## 10. Testing Strategy

### 10.1 Unit Tests

```java
@ExtendWith(MockitoExtension.class)
class TaskFeatureDispatcherTest {
    
    @Mock private UpdateTaskUseCaseImpl delegate;
    @Mock private FeatureTogglePort featureToggle;
    @Mock private TaskAuditFeatureHandler auditHandler;
    
    @InjectMocks
    private TaskFeatureDispatcher dispatcher;
    
    @Test
    void updateTask_whenAuditDisabled_shouldNotCallAuditHandler() {
        // Given
        when(featureToggle.isEnabled(any(), eq(Feature.AUDIT_LOG)))
            .thenReturn(false);
        
        // When
        dispatcher.updateTask(1L, createRequest());
        
        // Then
        verify(auditHandler, never()).before(any(), any());
        verify(auditHandler, never()).after(any(), any());
    }
    
    @Test
    void updateTask_whenAuditEnabled_shouldCallAuditHandler() {
        // Given
        when(featureToggle.isEnabled(any(), eq(Feature.AUDIT_LOG)))
            .thenReturn(true);
        
        // When
        dispatcher.updateTask(1L, createRequest());
        
        // Then
        verify(auditHandler).before(any(), any());
        verify(auditHandler).after(any(), any());
    }
}
```

### 10.2 Integration Tests

```java
@SpringBootTest
class FeatureFlagIntegrationTest {
    
    @Test
    void createProject_whenLimitExceeded_shouldThrowException() {
        // Given: Workspace at project limit
        
        // When/Then
        assertThrows(PlanLimitExceededException.class, () -> {
            createProjectUseCase.createProject(request);
        });
    }
}
```

---

## 11. Performance Considerations

| Metric | Target | Implementation |
|--------|--------|----------------|
| Feature check latency | < 5ms | Caffeine cache |
| Cache hit ratio | > 95% | 5-minute TTL |
| Memory overhead | < 50MB | Bounded cache size |
| Zero resource waste | 100% | Conditional execution |

---

## 12. Security Considerations

1. **Admin APIs** must be protected with ROLE_ADMIN
2. **Feature flags** are workspace-scoped, not user-scoped
3. **Cache invalidation** must be secure (only admins can trigger)
4. **Plan changes** should trigger audit logs

---

*Document Owner: Engineering Team*  
*Last Updated: February 28, 2026*
