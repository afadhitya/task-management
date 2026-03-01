# Feature Flag Implementation Plan
## Phase-Based Rollout Strategy

**Version:** 1.0  
**Date:** February 28, 2026  
**Status:** Draft  
**Related Documents:**
- [Feature Flag PRD](./feature-flag-prd.md)
- [Feature Flag Technical Documentation](./feature-flag-technical.md)

---

## Executive Summary

This document outlines a 4-phase implementation approach for the Feature Flag & Plan-Based Access Control system. Each phase delivers incremental value while building toward the complete solution.

| Phase | Duration | Focus | Deliverable |
|-------|----------|-------|-------------|
| **Phase 1** | 1 week | Foundation | Database + Core Infrastructure |
| **Phase 2** | 1 week | Audit Migration | Replace existing audit decorators |
| **Phase 3** | 1 week | Limits & APIs | Project/member limits + Admin APIs |
| **Phase 4** | 1 week | Polish & Expand | Notifications, search, documentation |

**Total Estimated Duration:** 4 weeks

---

## Phase 1: Foundation (Week 1)

### Goal
Establish the core infrastructure: database schema, domain models, and feature toggle service.

### Tasks

#### Day 1-2: Database & Domain Layer
```
┌─────────────────────────────────────────────────────────────┐
│  TASKS                                                      │
│  ├── Create Flyway migration V10__Create_feature_flag_system│
│  ├── Create Feature enum (domain/feature/Feature.java)      │
│  ├── Create LimitType enum (domain/feature/LimitType.java)  │
│  └── Create PlanLimitExceededException                      │
└─────────────────────────────────────────────────────────────┘
```

**Deliverables:**
- Migration script with seed data
- Domain enums for Feature and LimitType
- Custom exception for limit violations

**Verification:**
```bash
./gradlew flywayMigrate
# Verify tables created: features, plan_configurations, plan_features, plan_limits
```

---

#### Day 3-4: Application Layer - Core Interfaces
```
┌─────────────────────────────────────────────────────────────┐
│  TASKS                                                      │
│  ├── Create FeatureHandler<R, T> interface                  │
│  ├── Create FeatureContext class                            │
│  ├── Create FeatureTogglePort (output port)                 │
│  └── Create FeatureToggleAdapter (adapter implementation)   │
└─────────────────────────────────────────────────────────────┘
```

**Deliverables:**
- `application/port/feature/FeatureHandler.java`
- `application/port/feature/FeatureContext.java`
- `application/port/out/feature/FeatureTogglePort.java`
- `adapter/out/feature/FeatureToggleAdapter.java`

**Key Implementation Detail:**
```java
// FeatureToggleAdapter with caching
@Cacheable(value = "workspaceFeatures", key = "#workspaceId + ':' + #feature.code()")
public boolean isEnabled(Long workspaceId, Feature feature) { ... }
```

---

#### Day 5: Infrastructure & Configuration
```
┌─────────────────────────────────────────────────────────────┐
│  TASKS                                                      │
│  ├── Create CacheConfig (Caffeine cache)                    │
│  ├── Create AsyncConfig (for async feature handlers)        │
│  ├── Add cache properties to application.properties         │
│  └── Write unit tests for FeatureToggleAdapter              │
└─────────────────────────────────────────────────────────────┘
```

**Configuration:**
```properties
# application.properties
spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=10000,expireAfterWrite=5m
```

---

### Phase 1 Exit Criteria
- [ ] Database migration runs successfully
- [ ] FeatureToggleAdapter returns correct values from DB
- [ ] Cache is working (verified via logs/metrics)
- [ ] Unit tests pass for adapter

---

## Phase 2: Audit Migration (Week 2)

### Goal
Migrate ALL existing audit decorators to the new feature-aware pattern using a simplified interceptor approach. This proves the system works with a real feature while minimizing code changes.

### Approach: Simplified Interceptor Pattern (Option B)

Instead of creating 40+ dispatcher classes, we'll use a **single AuditFeatureInterceptor** that wraps all audit operations.

```
┌─────────────────────────────────────────────────────────────┐
│  SIMPLIFIED ARCHITECTURE                                    │
│                                                             │
│  Existing Decorator                                         │
│       │                                                     │
│       ▼                                                     │
│  ┌─────────────────────────────────────────────────────┐   │
│  │  Modified Audit Decorator                           │   │
│  │  ┌─────────────────────────────────────────────┐   │   │
│  │  │ 1. Check Feature Flag First                  │   │   │
│  │  │    if (!auditEnabled) return; // Skip all    │   │   │
│  │  └─────────────────────────────────────────────┘   │   │
│  │       │                                             │   │
│  │       ▼ (only if enabled)                           │   │
│  │  ┌─────────────────────────────────────────────┐   │   │
│  │  │ 2. Existing Audit Logic                      │   │   │
│  │  │    - Fetch old state                         │   │   │
│  │  │    - Calculate diff                          │   │   │
│  │  │    - Create audit log                        │   │   │
│  │  └─────────────────────────────────────────────┘   │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  Result: Zero DB reads when audit disabled                  │
└─────────────────────────────────────────────────────────────┘
```

### Tasks

#### Day 1: Create AuditFeatureInterceptor
```
┌─────────────────────────────────────────────────────────────┐
│  TASKS                                                      │
│  ├── Create AuditFeatureInterceptor                         │
│  │   ├── Check FeatureTogglePort.isEnabled()               │
│  │   ├── Early return if AUDIT_LOG disabled               │
│  │   └── Helper methods for common audit operations        │
│  └── Create AuditHelper utility class                       │
│      ├── buildCreateData()                                  │
│      ├── buildUpdateData()                                  │
│      └── buildDeleteData()                                  │
└─────────────────────────────────────────────────────────────┘
```

**Key Code:**
```java
@Component
@RequiredArgsConstructor
public class AuditFeatureInterceptor {
    
    private final FeatureTogglePort featureToggle;
    private final AuditLogService auditLogService;
    
    /**
     * Checks if audit is enabled before executing audit logic.
     * Returns true if audit should proceed, false to skip.
     */
    public boolean shouldAudit(Long workspaceId) {
        return featureToggle.isEnabled(workspaceId, Feature.AUDIT_LOG);
    }
    
    /**
     * Creates audit log if feature is enabled.
     */
    public void audit(Long workspaceId, Long actorId, AuditEntityType entityType,
                      Long entityId, AuditAction action, Map<String, Object> data) {
        if (!shouldAudit(workspaceId)) {
            return; // Zero resource waste
        }
        auditLogService.create(workspaceId, actorId, entityType, entityId, action, data);
    }
}
```

---

#### Day 2-3: Modify All Existing Audit Decorators
```
┌─────────────────────────────────────────────────────────────┐
│  ENTITIES TO MIGRATE (All at once)                          │
│                                                             │
│  1. Task (4 operations)                                     │
│     ├── CreateTask                                          │
│     ├── CreateSubtask                                       │
│     ├── UpdateTask                                          │
│     └── DeleteTask                                          │
│                                                             │
│  2. Project (3 operations)                                  │
│     ├── CreateProject                                       │
│     ├── UpdateProject                                       │
│     └── DeleteProject                                       │
│                                                             │
│  3. Workspace (7 operations)                                │
│     ├── CreateWorkspace                                     │
│     ├── UpdateWorkspace                                     │
│     ├── DeleteWorkspace                                     │
│     ├── InviteMember                                        │
│     ├── RemoveMember                                        │
│     ├── UpdateMemberRole                                    │
│     └── LeaveWorkspace                                      │
│                                                             │
│  4. Label (5 operations)                                    │
│     ├── CreateLabel                                         │
│     ├── UpdateLabel                                         │
│     ├── DeleteLabel                                         │
│     ├── AssignLabelToTask                                   │
│     └── RemoveLabelFromTask                                 │
│                                                             │
│  5. Comment (3 operations)                                  │
│     ├── CreateComment                                       │
│     ├── UpdateComment                                       │
│     └── DeleteComment                                       │
│                                                             │
│  TOTAL: 5 classes, 22 operations                            │
└─────────────────────────────────────────────────────────────┘
```

**Modification Pattern per Decorator:**
```java
@Service
@Primary
@RequiredArgsConstructor
public class AuditedTaskUseCases {
    
    private final TaskPersistencePort taskPersistencePort;
    private final AuditLogService auditLogService;
    private final AuditFeatureInterceptor auditInterceptor; // NEW
    
    @Service
    @Primary
    @RequiredArgsConstructor
    public class UpdateTask implements UpdateTaskUseCase {
        
        private final UpdateTaskUseCaseImpl delegate;
        
        @Override
        @Transactional
        public TaskResponse updateTask(Long id, UpdateTaskRequest request) {
            Task task = taskPersistencePort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
            
            Long workspaceId = task.getProject().getWorkspace().getId();
            
            // NEW: Check feature flag before any audit work
            boolean shouldAudit = auditInterceptor.shouldAudit(workspaceId);
            
            Map<String, Object> diff = new HashMap<>();
            if (shouldAudit) { // Only calculate diff if auditing
                if (request.title() != null && !request.title().equals(task.getTitle())) {
                    diff.put("title", Map.of("old", task.getTitle(), "new", request.title()));
                }
                // ... other fields
            }
            
            TaskResponse response = delegate.updateTask(id, request);
            
            // NEW: Use interceptor instead of direct service call
            if (shouldAudit && !diff.isEmpty()) {
                AuditAction action = diff.containsKey("status") ? 
                    AuditAction.STATUS_CHANGE : AuditAction.UPDATE;
                auditInterceptor.audit(workspaceId, SecurityUtils.getCurrentUserId(),
                    AuditEntityType.TASK, id, action, diff);
            }
            
            return response;
        }
    }
}
```

#### Day 5: Cleanup & Documentation
```
┌─────────────────────────────────────────────────────────────┐
│  TASKS                                                      │
│  ├── Verify all existing tests pass                         │
│  ├── Remove Audited*UseCases classes if fully migrated      │
│  │   (Decision: NO - keep modified versions)                │
│  ├── Update API documentation                               │
│  └── Document zero-resource-waste verification              │
└─────────────────────────────────────────────────────────────┘
```

### Phase 2 Exit Criteria
- [x] AuditFeatureInterceptor created and tested
- [x] All 5 Audited*UseCases classes modified with feature flag checks
- [x] All 22 audit operations check feature flag before DB reads
- [x] Zero DB reads verified when audit disabled (FREE plan)
- [x] Performance: feature check < 5ms (cached)

---

## Phase 2.5: Limit Enforcement Dispatchers

### Goal
Wire up the limit handlers (`ProjectLimitFeatureHandler`, `MemberLimitFeatureHandler`) to actually enforce plan limits when creating projects and inviting members.

### Problem
The limit handlers exist but are not being called. We need to integrate them into the request flow.

### Solution: Chained Dispatchers (Option B)

Chain the limit validation dispatcher BEFORE the audit decorator:

```
┌─────────────────────────────────────────────────────────────┐
│  Chained Dispatcher Flow                                    │
│                                                             │
│  Controller ──► LimitDispatcher (@Primary)                  │
│                      │                                      │
│                      ▼                                      │
│              Validate Limits                                │
│                      │                                      │
│              Exceeded? ──► Throw Exception                  │
│                      │                                      │
│                      ▼ (if valid)                           │
│              AuditedUseCase (existing @Primary inner class) │
│                      │                                      │
│                      ▼                                      │
│              UseCaseImpl (pure business logic)              │
└─────────────────────────────────────────────────────────────┘
```

**Key Points:**
- LimitDispatcher is `@Primary` at the class level
- LimitDispatcher injects the AuditedUseCase (not the Impl directly)
- AuditedUseCase remains unchanged, still handles audit logic
- Both limit validation AND audit are executed

### Tasks

#### Day 1: CreateProjectFeatureDispatcher
```
┌─────────────────────────────────────────────────────────────┐
│  TASKS                                                      │
│  ├── Create CreateProjectFeatureDispatcher                  │
│  │   ├── @Primary bean implementing CreateProjectUseCase    │
│  │   ├── Injects AuditedProjectUseCases.CreateProject       │
│  │   ├── Injects ProjectLimitFeatureHandler                 │
│  │   ├── Creates FeatureContext with workspaceId            │
│  │   ├── Calls handler.validate() before delegate           │
│  │   └── Delegates to audited use case if validation passes │
│  └── Test: FREE plan with 3 projects rejects 4th            │
│      └── Verify audit log still created when allowed        │
└─────────────────────────────────────────────────────────────┘
```

**Structure:**
```java
@Service
@Primary
@RequiredArgsConstructor
public class CreateProjectFeatureDispatcher implements CreateProjectUseCase {
    
    // Inject the AuditedUseCase (not Impl) to preserve audit
    private final AuditedProjectUseCases.CreateProject auditedCreateProject;
    private final ProjectLimitFeatureHandler limitHandler;
    
    @Override
    @Transactional
    public ProjectResponse createProject(CreateProjectRequest request, Long createdByUserId) {
        Long workspaceId = request.workspaceId();
        
        FeatureContext context = new FeatureContext(workspaceId, createdByUserId);
        
        // 1. Validate limits first
        limitHandler.validate(context, request);
        
        // 2. Delegate to audited use case (which will audit then call impl)
        return auditedCreateProject.createProject(request, createdByUserId);
    }
}
```

#### Day 2: InviteMemberFeatureDispatcher
```
┌─────────────────────────────────────────────────────────────┐
│  TASKS                                                      │
│  ├── Create InviteMemberFeatureDispatcher                   │
│  │   ├── @Primary bean implementing InviteMemberUseCase     │
│  │   ├── Injects AuditedWorkspaceUseCases.InviteMember      │
│  │   ├── Injects MemberLimitFeatureHandler                  │
│  │   ├── Creates FeatureContext with workspaceId            │
│  │   ├── Calls handler.validate() before delegate           │
│  │   └── Delegates to audited use case if validation passes │
│  └── Test: FREE plan with 5 members rejects 6th             │
│      └── Verify audit log still created when allowed        │
└─────────────────────────────────────────────────────────────┘
```

**Structure:**
```java
@Service
@Primary
@RequiredArgsConstructor
public class InviteMemberFeatureDispatcher implements InviteMemberUseCase {
    
    // Inject the AuditedUseCase (not Impl) to preserve audit
    private final AuditedWorkspaceUseCases.InviteMember auditedInviteMember;
    private final MemberLimitFeatureHandler limitHandler;
    
    @Override
    @Transactional
    public WorkspaceMemberResponse inviteMember(Long workspaceId, InviteMemberRequest request, Long currentUserId) {
        FeatureContext context = new FeatureContext(workspaceId, currentUserId);
        
        // 1. Validate limits first
        limitHandler.validate(context, request);
        
        // 2. Delegate to audited use case (which will audit then call impl)
        return auditedInviteMember.inviteMember(workspaceId, request, currentUserId);
    }
}
```

### Phase 2.5 Exit Criteria
- [ ] CreateProjectFeatureDispatcher enforces project limits
- [ ] InviteMemberFeatureDispatcher enforces member limits
- [ ] FREE plan rejects project creation when at 3 projects
- [ ] FREE plan rejects member invite when at 5 members
- [ ] TEAM/ENTERPRISE plans allow creation up to their limits
- [ ] Audit logs are still created when operations are allowed

---

## Phase 3: Limits & Admin APIs (Week 3)

### Goal
Implement admin configuration APIs for managing plan features and limits. Note: Limit enforcement is handled in Phase 2.5 by Feature Dispatchers.

### Tasks

#### Day 1-2: Admin APIs
```
┌─────────────────────────────────────────────────────────────┐
│  TASKS                                                      │
│  ├── Create AdminPlanController                             │
│  │   ├── GET /admin/plans - List plans                      │
│  │   ├── GET /admin/plans/:id - Get plan details            │
│  │   ├── PATCH /admin/plans/:id/features - Update features  │
│  │   └── PATCH /admin/plans/:id/limits - Update limits      │
│  ├── Create AdminPlanService                                │
│  ├── Create DTOs (PlanResponse, UpdateFeaturesRequest, ...) │
│  └── Add @PreAuthorize("hasRole('ADMIN')") security         │
└─────────────────────────────────────────────────────────────┘
```

**Controller:**
```java
@RestController
@RequestMapping("/admin/plans")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminPlanController {
    
    private final AdminPlanService adminPlanService;
    
    @GetMapping
    public ResponseEntity<List<PlanSummaryResponse>> listPlans() { ... }
    
    @GetMapping("/{id}")
    public ResponseEntity<PlanDetailResponse> getPlan(@PathVariable Long id) { ... }
    
    @PatchMapping("/{id}/features")
    public ResponseEntity<Void> updateFeatures(
        @PathVariable Long id,
        @RequestBody @Valid UpdateFeaturesRequest request
    ) { ... }
    
    @PatchMapping("/{id}/limits")
    public ResponseEntity<Void> updateLimits(
        @PathVariable Long id,
        @RequestBody @Valid UpdateLimitsRequest request
    ) { ... }
}
```

---

#### Day 5: Workspace Entitlement API
```
┌─────────────────────────────────────────────────────────────┐
│  TASKS                                                      │
│  ├── Create WorkspaceEntitlementService                     │
│  ├── Add GET /workspaces/:id/entitlements endpoint          │
│  ├── Calculate used vs remaining limits                     │
│  └── Write integration tests for admin APIs                 │
└─────────────────────────────────────────────────────────────┘
```

**Entitlement Response:**
```json
{
  "workspaceId": 123,
  "plan": { "tier": "FREE", "name": "Free Plan" },
  "features": [
    { "code": "NOTIFICATIONS", "name": "Notifications", "isEnabled": true },
    { "code": "AUDIT_LOG", "name": "Audit Log", "isEnabled": false }
  ],
  "limits": [
    { "type": "MAX_PROJECTS", "limit": 3, "used": 2, "remaining": 1 }
  ]
}
```

---

### Phase 3 Exit Criteria
- [x] Admin APIs working with proper security
- [x] Workspace entitlement endpoint returns correct data
- [x] Cache invalidation on plan configuration change

**Note:** Project/Member limit enforcement moved to Phase 2.5

---

## Phase 4: Polish & Expand (Week 4)

### Goal
Add remaining features (notifications, search), finalize documentation, and production hardening.

### Tasks

#### Day 1-2: Additional Feature Handlers
```
┌─────────────────────────────────────────────────────────────┐
│  TASKS                                                      │
│  ├── Create NotificationFeatureHandler (POST timing)        │
│  │   └── Send email/push notifications                      │
│  ├── Create SearchIndexFeatureHandler (ASYNC timing)        │
│  │   └── Index tasks/projects for search                    │
│  ├── Create WebhookFeatureHandler (ASYNC timing)            │
│  │   └── Trigger external webhooks                          │
│  └── Verify @Async execution works correctly                │
└─────────────────────────────────────────────────────────────┘
```

**Notification Handler:**
```java
@Component
@RequiredArgsConstructor
public class NotificationFeatureHandler implements FeatureHandler<UpdateTaskRequest, TaskResponse> {
    
    private final NotificationService notificationService;
    
    @Override
    public Feature getFeature() { return Feature.NOTIFICATIONS; }
    
    @Override
    public void after(FeatureContext context, TaskResponse result) {
        if (result.assigneeIds() != null && !result.assigneeIds().isEmpty()) {
            notificationService.sendTaskUpdatedNotification(
                result.id(), result.assigneeIds(), context.getActorId()
            );
        }
    }
}
```

---

#### Day 3: Attachment & Storage Limits
```
┌─────────────────────────────────────────────────────────────┐
│  TASKS                                                      │
│  ├── Create NotificationFeatureHandler (POST timing)        │
│  ├── Create SearchIndexFeatureHandler (ASYNC timing)        │
│  ├── Verify @Async execution works correctly                │
│  └── Add feature checks to attachment upload                │
└─────────────────────────────────────────────────────────────┘
```

---

#### Day 4: Error Handling & Edge Cases
```
┌─────────────────────────────────────────────────────────────┐
│  TASKS                                                      │
│  ├── Create GlobalExceptionHandler for PlanLimitExceeded    │
│  ├── Create error response DTOs                             │
│  ├── Handle plan downgrade scenarios                        │
│  │   └── Existing resources remain, new ones blocked        │
│  ├── Add graceful degradation for partial failures          │
│  └── Write edge case tests                                  │
└─────────────────────────────────────────────────────────────┘
```

**Error Response:**
```json
{
  "error": {
    "code": "PLAN_LIMIT_EXCEEDED",
    "message": "You have reached the maximum number of projects for your plan",
    "details": {
      "limitType": "MAX_PROJECTS",
      "limit": 3,
      "used": 3,
      "upgradeTo": "TEAM"
    }
  }
}
```

---

#### Day 5: Documentation & Final Testing
```
┌─────────────────────────────────────────────────────────────┐
│  TASKS                                                      │
│  ├── Update API documentation (OpenAPI/Swagger)             │
│  ├── Write runbook for plan configuration                   │
│  ├── Performance testing                                    │
│  │   ├── Feature check latency < 5ms                       │
│  │   └── Cache hit ratio > 95%                             │
│  ├── End-to-end testing                                     │
│  └── Final code review                                      │
└─────────────────────────────────────────────────────────────┘
```

---

### Phase 4 Exit Criteria
- [ ] Notification handler sends notifications when enabled
- [ ] Search indexing works asynchronously
- [ ] Webhook handler triggers external calls
- [ ] Attachment uploads respect storage limits
- [ ] Error handling polished with clear messages
- [ ] Performance targets met
- [ ] Documentation complete
- [ ] All tests passing

---

## Testing Strategy by Phase

### Phase 1 Tests
```
Unit Tests:
├── FeatureToggleAdapterTest
│   ├── isEnabled_returnsCorrectValue()
│   ├── isEnabled_usesCache()
│   └── invalidateCache_clearsCache()
└── FeatureContextTest
    ├── setAttributeAndGetAttribute_works()
    └── getAttribute_withWrongType_throws()
```

### Phase 2 Tests
```
Unit Tests:
├── AuditFeatureInterceptorTest
│   ├── shouldAudit_whenEnabled_returnsTrue()
│   ├── shouldAudit_whenDisabled_returnsFalse()
│   ├── audit_whenDisabled_doesNotCallService()
│   └── audit_whenEnabled_createsAuditLog()

Integration Tests (per entity):
├── TaskAuditInterceptorTest
│   ├── createTask_auditDisabled_noAuditCreated()
│   ├── updateTask_auditDisabled_noDiffCalculation()
│   ├── deleteTask_auditDisabled_noFetchOldState()
│   └── allOperations_auditEnabled_auditCreated()
├── ProjectAuditInterceptorTest
│   └── (same pattern)
├── WorkspaceAuditInterceptorTest
│   └── (same pattern)
├── LabelAuditInterceptorTest
│   └── (same pattern)
└── CommentAuditInterceptorTest
    └── (same pattern)
```

### Phase 3 Tests
```
Integration Tests:
├── ProjectLimitFeatureHandlerTest
│   ├── validate_underLimit_succeeds()
│   └── validate_overLimit_throwsException()
├── AdminPlanControllerTest
│   ├── listPlans_returnsAllPlans()
│   ├── updateFeatures_changesEnabledState()
│   └── updateLimits_changesLimitValue()
└── WorkspaceEntitlementTest
    ├── getEntitlements_returnsCorrectFeatures()
    └── getEntitlements_calculatesUsedLimits()
```

### Phase 4 Tests
```
End-to-End Tests:
├── FreePlanTest
│   ├── createProject_fourthProject_fails()
│   └── auditLog_notAvailable()
├── TeamPlanTest
│   └── advancedSearch_available()
└── EnterprisePlanTest
    └── allFeatures_available()
```

---

## Risk Mitigation

| Risk | Mitigation |
|------|------------|
| Cache inconsistency | Short TTL (5min) + explicit invalidation on plan change |
| Performance degradation | Benchmark before/after; feature check is cached |
| Migration breaking existing audit | Keep old decorators until new ones verified |
| Complex rollback | Each phase is independently deployable |
| Admin API security | @PreAuthorize on all admin endpoints |

---

## Implementation Decisions

### Phase 2 Approach: Simplified Interceptor (Approved)

| Decision | Option Chosen | Rationale |
|----------|---------------|-----------|
| **Architecture** | Option B: Simplified Interceptor | Less code (~10 files vs ~45 files), faster implementation, easier maintenance |
| **Scope** | All entities at once | Complete migration in one phase, consistent pattern across codebase |
| **Fallback** | Keep modified decorators | Safer approach, can rollback if issues; decorators modified not replaced |

### Alternative Approaches Considered

**Option A: Full Dispatcher Pattern (Rejected)**
- 40+ dispatcher classes, one per use case
- Complete clean architecture compliance
- More testable but significantly more code
- Longer implementation time

**Option B: Simplified Interceptor (Selected)**
- Single interceptor class + modified existing decorators
- Pragmatic balance of clean code and velocity
- Same zero-resource-waste benefit
- Easier to understand and maintain

---

## Rollback Plan

### Per Phase Rollback

**Phase 1 (Database):**
- Flyway undo migration available
- Old code doesn't use new tables (safe to keep)

**Phase 2 (Audit Migration):**
- Modified decorators keep same structure, just add early return
- Can comment out feature flag check to restore old behavior
- No structural changes to audit logic

**Phase 3 (Limits):**
- Limits can be set to -1 (unlimited) to disable
- Feature flags can disable limit checks

**Phase 4 (Final):**
- All changes additive or behind feature flags
- Database schema stable from Phase 1

---

## Success Metrics

| Phase | Metric | Target |
|-------|--------|--------|
| 1 | Migration runs successfully | 100% |
| 1 | Cache response time | < 5ms |
| 2 | DB reads saved (audit disabled) | 100% |
| 2 | Existing tests pass | 100% |
| 3 | Limit enforcement accuracy | 100% |
| 3 | Admin API response time | < 100ms |
| 4 | Overall feature check latency | P95 < 10ms |
| 4 | Cache hit ratio | > 95% |

---

## Post-Implementation

### Monitoring
- Feature check latency (APM metrics)
- Cache hit/miss ratio
- Plan limit violations (business metric)
- Feature usage by plan (product metric)

### Future Enhancements (v2)
- Real-time feature flag updates (WebSocket)
- Feature usage analytics dashboard
- A/B testing framework
- Granular per-user feature flags
- Scheduled feature enablement

---

*Document Owner: Engineering Team*  
*Last Updated: February 28, 2026*
